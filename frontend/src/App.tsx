import { Navigate, Route, Routes } from 'react-router-dom';
import type { ReactElement } from 'react';
import { useAuth } from './auth/AuthContext';
import { NavBar } from './components/NavBar';
import { LoginPage } from './pages/LoginPage';
import { OAuthCallbackPage } from './pages/OAuthCallbackPage';
import { DiscoverPage } from './pages/DiscoverPage';
import { MatchesPage } from './pages/MatchesPage';
import { ProfilePage } from './pages/ProfilePage';
import { LikesPage } from './pages/LikesPage';

function RequireAuth({ children }: { children: ReactElement }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="page-state">Loading…</div>;
  if (!user) return <Navigate to="/login" replace />;
  return children;
}

export default function App() {
  const { user } = useAuth();

  return (
    <div className="app">
      <NavBar />
      <main className="app__main">
        <Routes>
          <Route path="/login" element={user ? <Navigate to="/discover" replace /> : <LoginPage />} />
          <Route path='/oauth/callback' element={<OAuthCallbackPage/>} />
          <Route
            path="/discover"
            element={
              <RequireAuth>
                <DiscoverPage />
              </RequireAuth>
            }
          />
          <Route
            path='/likes'
            element={
              <RequireAuth>
                <LikesPage/>
              </RequireAuth>
            }
          />
          <Route
            path="/matches"
            element={
              <RequireAuth>
                <MatchesPage />
              </RequireAuth>
            }
          />
          <Route
            path="/profile"
            element={
              <RequireAuth>
                <ProfilePage />
              </RequireAuth>
            }
          />
          <Route path="*" element={<Navigate to={user ? '/discover' : '/login'} replace />} />
        </Routes>
      </main>
    </div>
  );
}
