import { useEffect, useState } from "react";
import { getWatchHistory } from "../api/watchApi";
import { getMovies } from "../api/contentApi";

export default function WatchHistory() {
  const [history, setHistory] = useState([]);
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadData = async () => {
      try {
        const [historyRes, moviesRes] = await Promise.all([
          getWatchHistory(),
          getMovies()
        ]);
        setHistory(historyRes.data);
        setMovies(moviesRes.data);
      } catch (err) {
        console.error("Error loading history", err);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  // Helper to find movie title by ID
  const getMovieTitle = (tmdbId) => {
    const movie = movies.find(m => m.tmdbId === tmdbId);
    return movie ? movie.title : `Movie ID: ${tmdbId}`;
  };

  if (loading) return <div className="text-white p-20 text-center">Loading...</div>;

  return (
    <div className="min-h-screen bg-black pt-24 px-8">
      <h1 className="text-white text-3xl font-bold mb-8">Watch History</h1>
      <div className="space-y-4 max-w-4xl">
        {history.map((item) => (
          <div key={item.id || Math.random()} className="bg-zinc-900 p-4 rounded-md flex justify-between items-center border-l-4 border-red-600">
            <div>
              <h3 className="text-white font-semibold text-lg">{getMovieTitle(item.tmdbMovieId)}</h3>
              <p className="text-zinc-500 text-sm">{new Date(item.watchedAt).toLocaleDateString()}</p>
            </div>
            <div className="text-right">
              <span className="text-zinc-400 text-xs block uppercase">Watch Time</span>
              <span className="text-white">{item.watchTime}s</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}