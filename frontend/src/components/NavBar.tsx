import { NavLink } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export function NavBar() {
  const { user, logout } = useAuth();
  if (!user) return null;

  return (
    <header className="navbar">
      <div className="navbar__brand">Fit<span>Mate</span></div>
      <nav className="navbar__links">
        <NavLink to="/discover" className={({ isActive }) => (isActive ? 'active' : '')}>
          Discover
        </NavLink>
        <NavLink to="/likes" className={({ isActive }) => (isActive ? 'active' : '')}>
          Likes You
        </NavLink>
        <NavLink to="/matches" className={({ isActive }) => (isActive ? 'active' : '')}>
          Matches
        </NavLink>
        <NavLink to="/profile" className={({ isActive }) => (isActive ? 'active' : '')}>
          Profile
        </NavLink>
      </nav>
      <button type="button" className="navbar__logout" onClick={logout}>
        Log out
      </button>
    </header>
  );
}
