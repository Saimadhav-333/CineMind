import requests
import os
import json
import time
from pymongo import MongoClient
from dotenv import load_dotenv
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

load_dotenv()

TMDB_API_KEY = os.getenv("TMDB_API_KEY")
MONGODB_URI = os.getenv("MONGODB_URI")

TMDB_BASE_URL = "https://api.themoviedb.org/3"
OUTPUT_FILE = "movies_2020_2026.json"

# ---------- HTTP SESSION WITH RETRIES ----------
session = requests.Session()

retry_strategy = Retry(
    total=5,
    backoff_factor=1.5,
    status_forcelist=[429, 500, 502, 503, 504],
    allowed_methods=["GET"]
)

adapter = HTTPAdapter(max_retries=retry_strategy)
session.mount("https://", adapter)
session.mount("http://", adapter)

DEFAULT_TIMEOUT = 15  # seconds

# ---------- MONGODB ----------
client = MongoClient(MONGODB_URI, serverSelectionTimeoutMS=5000)
db = client["contentdb"]
movies_collection = db["movies"]
movies_collection.create_index("tmdbId", unique=True)

# ---------- FUNCTIONS ----------
def safe_get(url, params):
    return session.get(url, params=params, timeout=DEFAULT_TIMEOUT)

def fetch_genres():
    url = f"{TMDB_BASE_URL}/genre/movie/list"
    params = {"api_key": TMDB_API_KEY}

    response = safe_get(url, params)
    response.raise_for_status()

    return {g["id"]: g["name"] for g in response.json()["genres"]}

def preload_movies(start_year, end_year, max_pages=10):
    genre_map = fetch_genres()
    all_movies = []

    for year in range(start_year, end_year + 1):
        print(f"\n📅 Loading movies for {year}")

        for page in range(1, max_pages + 1):
            try:
                url = f"{TMDB_BASE_URL}/discover/movie"
                params = {
                    "api_key": TMDB_API_KEY,
                    "primary_release_year": year,
                    "sort_by": "popularity.desc",
                    "page": page
                }

                response = safe_get(url, params)

                if response.status_code != 200:
                    print(f"⚠ TMDB error {response.status_code}")
                    break

                results = response.json().get("results", [])
                if not results:
                    break

                for movie in results:
                    doc = {
                        "tmdbId": movie["id"],
                        "title": movie["title"],
                        "language": movie.get("original_language"),
                        "posterPath": movie.get("poster_path"),
                        "genres": movie.get("genre_ids", [])
                    }

                    all_movies.append(doc)

                    try:
                        movies_collection.insert_one(doc)
                    except Exception:
                        pass  # duplicate

                print(f"  ✔ Page {page}")
                time.sleep(0.6)  # 🔥 VERY IMPORTANT (rate-limit safety)

            except Exception as e:
                print(f"❌ Error on year {year}, page {page}: {e}")
                time.sleep(5)  # cooldown
                continue

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(all_movies, f, indent=2)

    print(f"\n🎉 Completed. Saved {len(all_movies)} movies")

# ---------- RUN ----------
if __name__ == "__main__":
    preload_movies(2020, 2026)
