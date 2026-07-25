import { useEffect, useState } from 'react'
import { extractErrorMessage, likesApi, swipeApi } from '../api/client'
import { goalLabel } from '../api/types'
import type { IncomingLike } from '../api/types'

export function LikesPage() {
    const [likes, setLikes] = useState<IncomingLike[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [toast, setToast] = useState<string | null>(null);
    const [busyId, setBusyId] = useState<number | null>(null);

    const load = () => {
        setLoading(true);
        likesApi
          .received()
          .then(setLikes)
          .catch((err) => setError(extractErrorMessage(err, 'Could not load likes')))
          .finally(() => setLoading(false));
    };

    useEffect(load, []);

    const likeBack = async (like: IncomingLike) => {
        setBusyId(like.userId);
        try {
            const result = await swipeApi.swipe(like.userId, 'LIKE');
            //Liking back removes them from the pending list (now a match).
            setLikes((prev) => prev.filter((l) => l.userId !== like.userId));
            if (result.matched) {
                setToast(`It's a match with ${like.name}!!`);
                setTimeout(() => setToast(null), 3000);
            }
        } catch (err) {
            setError(extractErrorMessage(err, 'Could not like back'));
        } finally {
            setBusyId(null);
        }
    };

    if (loading) return <div className='page-state'>Loading...</div>;

    return (
        <div className='matches'>
            <h1>Likes you {likes.length > 0 ? <span className='badge'>{likes.length}</span> : null}</h1>
            {toast && <div className='toast' role='status'>{toast}</div>}
            {error && <div className='auth-form__error' role='alert'>{error}</div>}

            {likes.length ===0 ? (
                <p className='page-state'>No new likes yet. Keep swiping to get noticced!</p>
            ) : (
                <ul className='matches__list'>
                    {likes.map((like) => (
                        <li key={like.userId} className='match-item'>
                            <div className='match-item__avatar' aria-hidden>
                                {like.name.charAt(0).toUpperCase()}
                            </div>
                            <div className='match-item__info'>
                                <span className='match-item__name'>
                                    {like.name}
                                    {like.age ? `, ${like.age}` : ''}
                                </span>
                                <span className='match-item__meta'>
                                    {goalLabel(like.workoutGoal)}
                                    {like.gymName ? ` . ${like.gymName}` : ''}
                                    {like.city ? ` . ${like.city}` : ''}
                                </span>
                            </div>
                            <button
                                type='button'
                                className='btn btn--like match-item__action'
                                disabled={busyId === like.userId}
                                onClick={() => likeBack(like)}
                            >
                                {busyId === like.userId ? '....' : 'Like back hogya'}
                            </button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}