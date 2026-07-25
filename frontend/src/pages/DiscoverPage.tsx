import { useCallback, useEffect, useState } from 'react';
import { SwipeCard } from '../components/SwipeCard';
import { discoveryApi, extractErrorMessage, swipeApi } from '../api/client';
import type { CandidateCard, SwipeDirection } from '../api/types';

export function DiscoverPage() {
  const [cards, setCards] = useState<CandidateCard[]>([]);
  const [index, setIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const loadFeed = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const feed = await discoveryApi.feed();
      setCards(feed);
      setIndex(0);
    } catch (err) {
      setError(extractErrorMessage(err, 'Could not load your feed'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadFeed();
  }, [loadFeed]);

  const current = cards[index];

  const handleSwipe = async (direction: SwipeDirection) => {
    if (!current || busy) return;
    setBusy(true);
    try {
      const result = await swipeApi.swipe(current.id, direction);
      if (result.matched) {
        setToast(`🎉 It's a match with ${current.name}!`);
        setTimeout(() => setToast(null), 3000);
      }
      setIndex((i) => i + 1);
    } catch (err) {
      setError(extractErrorMessage(err, 'Swipe failed'));
    } finally {
      setBusy(false);
    }
  };

  if (loading) return <div className="page-state">Loading your feed…</div>;
  if (error) {
    return (
      <div className="page-state">
        <p>{error}</p>
        <button type="button" className="btn btn--primary" onClick={loadFeed}>
          Retry
        </button>
      </div>
    );
  }

  return (
    <div className="discover">
      {toast && <div className="toast" role="status">{toast}</div>}
      {current ? (
        <SwipeCard
          card={current}
          disabled={busy}
          onLike={() => handleSwipe('LIKE')}
          onPass={() => handleSwipe('PASS')}
        />
      ) : (
        <div className="page-state">
          <h2>You're all caught up 💪</h2>
          <p>No more gym partners to show right now.</p>
          <button type="button" className="btn btn--primary" onClick={loadFeed}>
            Refresh feed
          </button>
        </div>
      )}
    </div>
  );
}
