from typing import List
from pydantic import BaseModel


class WatchHistoryItem(BaseModel):
    tmdbMovieId: int
    watchTime: int


class CandidateMovie(BaseModel):
    tmdbId: int
    genres: List[str]


class RecommendationRequest(BaseModel):
    userId: str
    watchHistory: List[WatchHistoryItem]
    candidateMovies: List[CandidateMovie]
    limit: int = 10


class RecommendationResponse(BaseModel):
    recommendedMovieIds: List[int]
