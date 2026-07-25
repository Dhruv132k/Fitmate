import { useState } from 'react';
import type { FormEvent } from 'react';
import { useAuth } from '../auth/AuthContext';
import { extractErrorMessage, profileApi } from '../api/client';
import { EXPERIENCE_LEVELS, WORKOUT_GOALS, goalLabel } from '../api/types';
import type { ExperienceLevel, WorkoutGoal } from '../api/types';

export function ProfilePage() {
  const { user, refresh } = useAuth();

  const [name, setName] = useState(user?.name ?? '');
  const [bio, setBio] = useState(user?.bio ?? '');
  const [age, setAge] = useState<string>(user?.age ? String(user.age) : '');
  const [workoutGoal, setWorkoutGoal] = useState<WorkoutGoal>(user?.workoutGoal ?? 'MUSCLE_GAIN');
  const [experienceLevel, setExperienceLevel] = useState<ExperienceLevel>(
    user?.experienceLevel ?? 'INTERMEDIATE',
  );
  const [gymName, setGymName] = useState(user?.gymName ?? '');
  const [city, setCity] = useState(user?.city ?? '');
  const [status, setStatus] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const save = async (e: FormEvent) => {
    e.preventDefault();
    setStatus(null);
    setError(null);
    setSaving(true);
    try {
      await profileApi.update({
        name,
        bio,
        age: age ? Number(age) : undefined,
        workoutGoal,
        experienceLevel,
        gymName,
        city,
      });
      await refresh();
      setStatus('Profile updated');
    } catch (err) {
      setError(extractErrorMessage(err, 'Could not save profile'));
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="profile">
      <h1>Your profile</h1>
      <form onSubmit={save} className="auth-form">
        <label>
          Name
          <input type="text" value={name} onChange={(e) => setName(e.target.value)} />
        </label>
        <label>
          Age
          <input type="number" min={13} max={120} value={age} onChange={(e) => setAge(e.target.value)} />
        </label>
        <label>
          Bio
          <textarea value={bio} maxLength={500} onChange={(e) => setBio(e.target.value)} />
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

        {status && <div className="auth-form__status" role="status">{status}</div>}
        {error && <div className="auth-form__error" role="alert">{error}</div>}

        <button type="submit" className="btn btn--primary" disabled={saving}>
          {saving ? 'Saving…' : 'Save changes'}
        </button>
      </form>
    </div>
  );
}
