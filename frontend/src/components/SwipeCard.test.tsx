import { describe, expect, it, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { SwipeCard } from './SwipeCard';
import type { CandidateCard } from '../api/types';

const card: CandidateCard = {
  id: 2,
  name: 'Alex',
  age: 27,
  bio: 'Push pull legs',
  workoutGoal: 'MUSCLE_GAIN',
  experienceLevel: 'INTERMEDIATE',
  gymName: 'Iron Paradise',
  city: 'Pune',
  matchReasons: ['Same goal: muscle gain', 'Trains at Iron Paradise'],
  compatibilityScore: 85,
};

describe('SwipeCard', () => {
  it('renders candidate details, score and match reasons', () => {
    render(<SwipeCard card={card} onLike={() => {}} onPass={() => {}} />);

    expect(screen.getByText('Alex')).toBeInTheDocument();
    expect(screen.getByText(/, 27/)).toBeInTheDocument();
    expect(screen.getByText('Muscle Gain')).toBeInTheDocument();
    expect(screen.getByText('85%')).toBeInTheDocument();
    expect(screen.getByText('Same goal: muscle gain')).toBeInTheDocument();
    expect(screen.getByText(/Iron Paradise/)).toBeInTheDocument();
  });

  it('fires onLike and onPass callbacks', async () => {
    const onLike = vi.fn();
    const onPass = vi.fn();
    render(<SwipeCard card={card} onLike={onLike} onPass={onPass} />);

    await userEvent.click(screen.getByRole('button', { name: 'Like' }));
    await userEvent.click(screen.getByRole('button', { name: 'Pass' }));

    expect(onLike).toHaveBeenCalledTimes(1);
    expect(onPass).toHaveBeenCalledTimes(1);
  });

  it('disables actions when disabled', () => {
    render(<SwipeCard card={card} onLike={() => {}} onPass={() => {}} disabled />);
    expect(screen.getByRole('button', { name: 'Like' })).toBeDisabled();
    expect(screen.getByRole('button', { name: 'Pass' })).toBeDisabled();
  });
});
