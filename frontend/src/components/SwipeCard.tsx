import type { CandidateCard } from '../api/types';
import { goalLabel } from '../api/types';

interface Props {
  card: CandidateCard;
  onLike: () => void;
  onPass: () => void;
  disabled?: boolean;
}

export function SwipeCard({ card, onLike, onPass, disabled }: Props) {
  return (
    <div className="swipe-card" data-testid="swipe-card">
      <div className="swipe-card__score" title="Compatibility score">
        {card.compatibilityScore}%
      </div>
      <div className="swipe-card__avatar" aria-hidden>
        {card.name.charAt(0).toUpperCase()}
      </div>
      <h2 className="swipe-card__name">
        {card.name}
        {card.age ? <span className="swipe-card__age">, {card.age}</span> : null}
      </h2>
      <div className="swipe-card__goal">{goalLabel(card.workoutGoal)}</div>

      <ul className="swipe-card__meta">
        {card.gymName ? <li>🏋️ {card.gymName}</li> : null}
        {card.city ? <li>📍 {card.city}</li> : null}
        {card.experienceLevel ? <li>⭐ {goalLabel(card.experienceLevel)}</li> : null}
      </ul>

      {card.bio ? <p className="swipe-card__bio">{card.bio}</p> : null}

      {card.matchReasons.length > 0 ? (
        <div className="swipe-card__reasons">
          {card.matchReasons.map((reason) => (
            <span key={reason} className="chip">
              {reason}
            </span>
          ))}
        </div>
      ) : null}

      <div className="swipe-card__actions">
        <button
          type="button"
          className="btn btn--pass"
          onClick={onPass}
          disabled={disabled}
          aria-label="Pass"
        >
          ✕ Pass
        </button>
        <button
          type="button"
          className="btn btn--like"
          onClick={onLike}
          disabled={disabled}
          aria-label="Like"
        >
          ♥ Like
        </button>
      </div>
    </div>
  );
}
