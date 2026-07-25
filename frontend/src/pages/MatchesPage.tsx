import { useEffect, useState } from 'react';
import { extractErrorMessage, matchApi } from '../api/client';
import { goalLabel } from '../api/types';
import type { MatchResponse } from '../api/types';

export function MatchesPage() {
  const [matches, setMatches] = useState<MatchResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    matchApi
      .list()
      .then(setMatches)
      .catch((err) => setError(extractErrorMessage(err, 'Could not load matches')))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="page-state">Loading matches…</div>;
  if (error) return <div className="page-state">{error}</div>;

  return (
    <div className="matches">
      <h1>Your matches</h1>
      {matches.length === 0 ? (
        <p className="page-state">No matches yet. Keep swiping!</p>
      ) : (
        <ul className="matches__list">
          {matches.map((m) => (
            <li key={m.matchId} className="match-item">
              <div className="match-item__avatar" aria-hidden>
                {m.name.charAt(0).toUpperCase()}
              </div>
              <div className="match-item__info">
                <span className="match-item__name">
                  {m.name}
                  {m.age ? `, ${m.age}` : ''}
                </span>
                <span className="match-item__meta">
                  {goalLabel(m.workoutGoal)}
                  {m.gymName ? ` · ${m.gymName}` : ''}
                  {m.city ? ` · ${m.city}` : ''}
                </span>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
