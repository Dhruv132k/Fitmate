import { describe, expect, it, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { DiscoverPage } from './DiscoverPage';
import { discoveryApi, swipeApi } from '../api/client';
import type { CandidateCard } from '../api/types';

vi.mock('../api/client', () => ({
  discoveryApi: { feed: vi.fn() },
  swipeApi: { swipe: vi.fn() },
  extractErrorMessage: (_e: unknown, fallback: string) => fallback,
}));

const cards: CandidateCard[] = [
  {
    id: 2,
    name: 'Alex',
    workoutGoal: 'MUSCLE_GAIN',
    matchReasons: [],
    compatibilityScore: 50,
  },
];

describe('DiscoverPage', () => {
  beforeEach(() => {
    vi.mocked(discoveryApi.feed).mockResolvedValue(cards);
    vi.mocked(swipeApi.swipe).mockResolvedValue({ matched: false, message: 'ok' });
  });

  it('loads the feed and shows the first card', async () => {
    render(<DiscoverPage />);
    expect(await screen.findByText('Alex')).toBeInTheDocument();
  });

  it('advances to empty state after swiping the only card', async () => {
    render(<DiscoverPage />);
    await screen.findByText('Alex');

    await userEvent.click(screen.getByRole('button', { name: 'Like' }));

    await waitFor(() => expect(swipeApi.swipe).toHaveBeenCalledWith(2, 'LIKE'));
    expect(await screen.findByText(/all caught up/i)).toBeInTheDocument();
  });

  it('shows a match toast when a swipe results in a match', async () => {
    vi.mocked(swipeApi.swipe).mockResolvedValue({ matched: true, matchId: 9, message: "It's a match!" });
    render(<DiscoverPage />);
    await screen.findByText('Alex');

    await userEvent.click(screen.getByRole('button', { name: 'Like' }));

    expect(await screen.findByText(/It's a match with Alex/)).toBeInTheDocument();
  });
});
