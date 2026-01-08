import { addToHistory } from "../api/watchApi";

// TMDB Genre ID Mapping
const GENRE_MAP = {
  "28": "Action", "12": "Adventure", "16": "Animation", "35": "Comedy",
  "80": "Crime", "99": "Documentary", "18": "Drama", "10751": "Family",
  "14": "Fantasy", "36": "History", "27": "Horror", "10402": "Music",
  "9648": "Mystery", "10749": "Romance", "878": "Sci-Fi", "10770": "TV Movie",
  "53": "Thriller", "10752": "War", "37": "Western"
};

export default function MovieCard({ movie }) {
  const IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

  const handleWatch = async (e) => {
    e.stopPropagation();
    try {
      // API expects tmdbMovieId as a query param
      await addToHistory(movie.tmdbId, 0); 
      alert(`Added to history: ${movie.title}`);
    } catch (err) {
      console.error("Failed to update history", err);
    }
  };

  return (
    <div className="relative group cursor-pointer transition-transform duration-300 hover:scale-105 hover:z-10 bg-zinc-900 rounded-md overflow-hidden">
      <img
        src={`${IMAGE_BASE_URL}${movie.posterPath}`}
        alt={movie.title}
        className="w-full h-auto object-cover"
      />
      
      {/* Hover Overlay */}
      <div className="absolute inset-0 bg-black/80 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex flex-col justify-end p-4">
        <h3 className="text-white font-bold text-sm mb-1">{movie.title}</h3>
        
        {/* Genre Tags */}
        <div className="flex flex-wrap gap-1 mb-3">
          {movie.genres?.slice(0, 3).map((id) => (
            <span key={id} className="text-[10px] text-gray-400 bg-zinc-800 px-1.5 py-0.5 rounded">
              {GENRE_MAP[id] || "Other"}
            </span>
          ))}
        </div>
        
        <button 
          onClick={handleWatch}
          className="bg-red-600 text-white text-xs font-bold py-2 rounded hover:bg-red-700 transition w-full"
        >
          Watch Now
        </button>
      </div>
    </div>
  );
}