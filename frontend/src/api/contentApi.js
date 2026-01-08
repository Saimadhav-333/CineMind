import api from "./axios";

// Now use the standard 'api' instance because 8080 handles both
export const getMovies = () => {
  return api.get("/content/movies");
};

export const syncMovies = () => {
  // This now goes to http://localhost:8080/admin/sync-movies
  // and the Gateway routes it to 8081
  return api.post("/admin/sync-movies"); 
};