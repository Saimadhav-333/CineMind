import { useState } from "react";
import { loginUser } from "../api/authApi";
import { useAuth } from "../context/AuthContext";
import { useNavigate, Link } from "react-router-dom";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await loginUser({ username, password });
      login(res.data.token);
      navigate("/dashboard");
    } catch (err) {
      alert("Login failed. Please check your credentials.");
    }
  };

  return (
    <div className="relative min-h-screen w-full flex items-center justify-center bg-black">
      {/* Background Overlay */}
      <div className="absolute inset-0 opacity-50 bg-[url('https://assets.nflxext.com/ffe/siteui/vlv3/f8640915-9c20-48ee-a294-2401ec935150/netflix-bg.jpg')] bg-cover"></div>
      
      <form
        onSubmit={handleSubmit}
        className="relative z-10 bg-black/75 p-16 rounded-md w-full max-w-md"
      >
        <h2 className="text-3xl font-bold mb-8 text-white">Sign In</h2>

        <input
          type="text"
          placeholder="Username"
          className="w-full mb-4 p-4 bg-zinc-800 text-white border-none rounded focus:ring-2 focus:ring-zinc-500 outline-none"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />

        <input
          type="password"
          placeholder="Password"
          className="w-full mb-8 p-4 bg-zinc-800 text-white border-none rounded focus:ring-2 focus:ring-zinc-500 outline-none"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button
          type="submit"
          className="w-full bg-red-600 text-white py-3 rounded font-bold hover:bg-red-700 transition"
        >
          Sign In
        </button>

        <p className="mt-12 text-zinc-500">
          New to Netflix?{" "}
          <Link to="/register" className="text-white hover:underline">
            Sign up now.
          </Link>
        </p>
      </form>
    </div>
  );
}