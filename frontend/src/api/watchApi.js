import api from "./axios";

// Fetches the user's watch history
export const getWatchHistory = () => {
  return api.get("/watch");
};

// Adds a movie to the watch history
// We pass tmdbMovieId and a default watchTime (e.g., 0 or progress)
export const addToHistory = (tmdbMovieId, watchTime = 0) => {
  return api.post(`/watch?tmdbMovieId=${tmdbMovieId}&watchTime=${watchTime}`);
};