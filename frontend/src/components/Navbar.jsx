import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const { token, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="fixed top-0 w-full z-50 bg-black/90 px-8 py-4 flex justify-between items-center">
      <div className="flex items-center gap-8">
        <Link to="/dashboard" className="text-red-600 text-3xl font-bold tracking-tighter">
          CINEMIND
        </Link>
        {token && (
          <div className="hidden md:flex gap-6 text-sm text-gray-300">
            <Link to="/dashboard" className="hover:text-white transition">Home</Link>
            <Link to="/recommendations" className="hover:text-white transition">Recommendations</Link>
            <Link to="/history" className="hover:text-white transition">History</Link>
          </div>
        )}
      </div>

      <div className="flex items-center gap-4">
        {token ? (
          <button
            onClick={handleLogout}
            className="bg-red-600 text-white px-4 py-1.5 rounded text-sm font-medium hover:bg-red-700 transition"
          >
            Sign Out
          </button>
        ) : (
          <Link
            to="/login"
            className="bg-red-600 text-white px-4 py-1.5 rounded text-sm font-medium hover:bg-red-700 transition"
          >
            Sign In
          </Link>
        )}
      </div>
    </nav>
  );
}