from fastapi import FastAPI
from app.schemas import RecommendationRequest, RecommendationResponse
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from collections import defaultdict
import math

app = FastAPI(title="AI Recommendation Service")


@app.get("/health")
def health_check():
    return {"status": "UP"}


@app.post("/recommend", response_model=RecommendationResponse)
def recommend_movies(request: RecommendationRequest):

    # -------------------------------
    # 1️⃣ Edge cases
    # -------------------------------
    if not request.watchHistory or not request.candidateMovies:
        return RecommendationResponse(recommendedMovieIds=[])

    # -------------------------------
    # 2️⃣ Build genre vocabulary
    # -------------------------------
    genre_set = set()
    for movie in request.candidateMovies:
        genre_set.update(movie.genres)

    genre_list = sorted(list(genre_set))
    genre_index = {genre: idx for idx, genre in enumerate(genre_list)}
    vector_size = len(genre_list)

    # -------------------------------
    # 3️⃣ Build movie vectors
    # -------------------------------
    movie_vectors = []
    movie_ids = []
    movie_genres_map = {}

    for movie in request.candidateMovies:
        vector = np.zeros(vector_size)
        for genre in movie.genres:
            vector[genre_index[genre]] = 1
        movie_vectors.append(vector)
        movie_ids.append(movie.tmdbId)
        movie_genres_map[movie.tmdbId] = movie.genres

    movie_vectors = np.array(movie_vectors)

    # -------------------------------
    # 4️⃣ Build user preference vector
    #    (recency + smoothing)
    # -------------------------------
    user_vector = np.zeros(vector_size)

    # Most recent watch gets highest weight
    total_watches = len(request.watchHistory)

    for index, watched in enumerate(request.watchHistory):
        # 🔹 Recency factor (recent → higher weight)
        recency_factor = (index + 1) / total_watches  # 0 < f ≤ 1

        # 🔹 Smooth watch time
        smoothed_watch_time = math.sqrt(watched.watchTime)

        effective_weight = recency_factor * smoothed_watch_time

        # Match watched movie genres (best-effort)
        for movie in request.candidateMovies:
            if movie.tmdbId == watched.tmdbMovieId:
                for genre in movie.genres:
                    user_vector[genre_index[genre]] += effective_weight

    # Fallback if vector is empty
    if np.linalg.norm(user_vector) == 0:
        return RecommendationResponse(
            recommendedMovieIds=movie_ids[: request.limit]
        )

    user_vector = user_vector.reshape(1, -1)

    # -------------------------------
    # 5️⃣ Cosine similarity
    # -------------------------------
    similarities = cosine_similarity(user_vector, movie_vectors)[0]

    # -------------------------------
    # 6️⃣ Rank movies by similarity
    # -------------------------------
    ranked_indices = np.argsort(similarities)[::-1]
    ranked_movie_ids = [movie_ids[i] for i in ranked_indices]

    # -------------------------------
    # 7️⃣ Apply genre diversity constraint
    # -------------------------------
    MAX_PER_GENRE = 3
    genre_count = defaultdict(int)
    final_recommendations = []

    for movie_id in ranked_movie_ids:
        genres = movie_genres_map.get(movie_id, [])
        if all(genre_count[g] < MAX_PER_GENRE for g in genres):
            final_recommendations.append(movie_id)
            for g in genres:
                genre_count[g] += 1

        if len(final_recommendations) >= request.limit:
            break

    # -------------------------------
    # 8️⃣ Return result
    # -------------------------------
    return RecommendationResponse(
        recommendedMovieIds=final_recommendations
    )
