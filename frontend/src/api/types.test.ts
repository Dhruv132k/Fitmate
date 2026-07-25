import { describe, expect, it } from 'vitest';
import { goalLabel } from './types';
import { tokenStore, extractErrorMessage } from './client';

describe('goalLabel', () => {
  it('formats enum values into title case', () => {
    expect(goalLabel('MUSCLE_GAIN')).toBe('Muscle Gain');
    expect(goalLabel('WEIGHT_LOSS')).toBe('Weight Loss');
    expect(goalLabel('CROSSFIT')).toBe('Crossfit');
  });
});

describe('tokenStore', () => {
  it('stores, reads and clears the token', () => {
    expect(tokenStore.get()).toBeNull();
    tokenStore.set('abc');
    expect(tokenStore.get()).toBe('abc');
    tokenStore.clear();
    expect(tokenStore.get()).toBeNull();
  });
});

describe('extractErrorMessage', () => {
  it('falls back to the default for non-axios errors', () => {
    expect(extractErrorMessage({}, 'oops')).toBe('oops');
  });
});
