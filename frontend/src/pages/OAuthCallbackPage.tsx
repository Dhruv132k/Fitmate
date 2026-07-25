import { useEffect, useRef, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { tokenStore } from '../api/client';
import { useAuth } from '../auth/AuthContext';

/**
 * Landing page for the backend's post-OAuth redirect. The backend appends
 * ?token=<jwt> (or ?error=...). We store the token, load the profile, and go
 * to the app - or bounce back to login on error.
 */
export function OAuthCallbackPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const { refresh } = useAuth();
  const [message, setMessage] = useState('Signing you in…');
  const handled = useRef(false);

  useEffect(() => {
    if (handled.current) return;
    handled.current = true;

    const token = params.get('token');
    const error = params.get('error');

    if (error || !token) {
      navigate(`/login?error=${encodeURIComponent(error ?? 'oauth_failed')}`, { replace: true });
      return;
    }

    tokenStore.set(token);
    refresh()
      .then(() => navigate('/discover', { replace: true }))
      .catch(() => {
        tokenStore.clear();
        setMessage('Sign-in failed. Redirecting…');
        navigate('/login?error=profile_load_failed', { replace: true });
      });
  }, [params, navigate, refresh]);

  return <div className="page-state">{message}</div>;
}