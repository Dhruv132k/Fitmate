export type WorkoutGoal =
  | 'WEIGHT_LOSS'
  | 'MUSCLE_GAIN'
  | 'STRENGTH'
  | 'ENDURANCE'
  | 'POWERLIFTING'
  | 'CROSSFIT'
  | 'GENERAL_FITNESS'
  | 'BODYBUILDING';

export type ExperienceLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';

export type SwipeDirection = 'LIKE' | 'PASS';

export interface UserProfile {
  id: number;
  email: string;
  name: string;
  age?: number;
  bio?: string;
  workoutGoal: WorkoutGoal;
  experienceLevel?: ExperienceLevel;
  gymName?: string;
  city?: string;
  latitude?: number;
  longitude?: number;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: UserProfile;
}

export interface CandidateCard {
  id: number;
  name: string;
  age?: number;
  bio?: string;
  workoutGoal: WorkoutGoal;
  experienceLevel?: ExperienceLevel;
  gymName?: string;
  city?: string;
  matchReasons: string[];
  compatibilityScore: number;
}

export interface SwipeResult {
  matched: boolean;
  matchId?: number;
  message: string;
}

export interface MatchResponse {
  matchId: number;
  userId: number;
  name: string;
  age?: number;
  bio?: string;
  workoutGoal: WorkoutGoal;
  gymName?: string;
  city?: string;
  matchedAt: string;
}

export interface IncomingLike {
  userId: number;
  name: string;
  age?: number;
  bio?: number;
  workoutGoal: WorkoutGoal;
  gymName?: string;
  city?: string;
  likedAt: string;
}

export interface RegisterPayload {
  email: string;
  password: string;
  name: string;
  workoutGoal: WorkoutGoal;
  experienceLevel?: ExperienceLevel;
  gymName?: string;
  city?: string;
  age?: number;
  bio?: string;
}

export const WORKOUT_GOALS: WorkoutGoal[] = [
  'WEIGHT_LOSS',
  'MUSCLE_GAIN',
  'STRENGTH',
  'ENDURANCE',
  'POWERLIFTING',
  'CROSSFIT',
  'GENERAL_FITNESS',
  'BODYBUILDING',
];

export const EXPERIENCE_LEVELS: ExperienceLevel[] = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED'];

export function goalLabel(goal: WorkoutGoal): string {
  return goal
    .toLowerCase()
    .split('_')
    .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
    .join(' ');
}
