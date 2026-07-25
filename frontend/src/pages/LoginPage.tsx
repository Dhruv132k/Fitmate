import { useState } from 'react';
import type { FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { extractErrorMessage, oauthLoginUrl } from '../api/client';
import { EXPERIENCE_LEVELS, WORKOUT_GOALS, goalLabel } from '../api/types';
import type { ExperienceLevel, WorkoutGoal } from '../api/types';

export function LoginPage() {
  const { login, register } = useAuth();
  const navigate = useNavigate();

  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [workoutGoal, setWorkoutGoal] = useState<WorkoutGoal>('MUSCLE_GAIN');
  const [experienceLevel, setExperienceLevel] = useState<ExperienceLevel>('INTERMEDIATE');
  const [gymName, setGymName] = useState('');
  const [city, setCity] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      if (mode === 'login') {
        await login(email, password);
      } else {
        await register({ email, password, name, workoutGoal, experienceLevel, gymName, city });
      }
      navigate('/discover');
    } catch (err) {
      setError(extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-screen">
      <div className="auth-card">
        <div className="auth-card__brand">Fit<span>Mate</span></div>
        <p className="auth-card__tagline">Find your perfect gym partner.</p>

        <div className="auth-card__tabs">
          <button
            type="button"
            className={mode === 'login' ? 'active' : ''}
            onClick={() => setMode('login')}
          >
            Log in
          </button>
          <button
            type="button"
            className={mode === 'register' ? 'active' : ''}
            onClick={() => setMode('register')}
          >
            Sign up
          </button>
        </div>

        <form onSubmit={submit} className="auth-form">
          <label>
            Email
            <input
              type="email"
              value={email}
              required
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
            />
          </label>
          <label>
            Password
            <input
              type="password"
              value={password}
              required
              minLength={8}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
            />
          </label>

          {mode === 'register' && (
            <>
              <label>
                Name
                <input type="text" value={name} required onChange={(e) => setName(e.target.value)} />
              </label>
              <label>
                Workout goal
                <select value={workoutGoal} onChange={(e) => setWorkoutGoal(e.target.value as WorkoutGoal)}>
                  {WORKOUT_GOALS.map((g) => (
                    <option key={g} value={g}>
                      {goalLabel(g)}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Experience
                <select
                  value={experienceLevel}
                  onChange={(e) => setExperienceLevel(e.target.value as ExperienceLevel)}
                >
                  {EXPERIENCE_LEVELS.map((l) => (
                    <option key={l} value={l}>
                      {goalLabel(l)}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Gym name
                <input type="text" value={gymName} onChange={(e) => setGymName(e.target.value)} />
              </label>
              <label>
                City
                <input type="text" value={city} onChange={(e) => setCity(e.target.value)} />
              </label>
            </>
          )}

          {error && <div className="auth-form__error" role="alert">{error}</div>}

          <button type="submit" className="btn btn--primary" disabled={submitting}>
            {submitting ? 'Please wait…' : mode === 'login' ? 'Log in' : 'Create account'}
          </button>
        </form>

        <div className="auth-divider"><span>or continue with</span></div>

        <div className="oauth-buttons">
          <button
            type="button"
            className="btn btn--oauth"
            onClick={() => {
              window.location.href = oauthLoginUrl('google');
            }}
          >
            <span aria-hidden>G</span> Google
          </button>
          <button
            type="button"
            className="btn btn--oauth"
            onClick={() => {
              window.location.href = oauthLoginUrl('github');
            }}
          >
            <span aria-hidden>⌥</span> GitHub
          </button>
        </div>
      </div>
    </div>
  );
}