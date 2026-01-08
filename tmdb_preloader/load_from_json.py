import json
import os
from pymongo import MongoClient
from dotenv import load_dotenv

load_dotenv()

MONGODB_URI = os.getenv("MONGODB_URI")
JSON_FILE = "movies_2020_2026.json"

client = MongoClient(MONGODB_URI)
db = client["contentdb"]
movies_collection = db["movies"]
movies_collection.create_index("tmdbId", unique=True)

def load_movies_from_json():
    with open(JSON_FILE, "r", encoding="utf-8") as f:
        movies = json.load(f)

    inserted = 0
    for movie in movies:
        try:
            movies_collection.insert_one(movie)
            inserted += 1
        except Exception:
            pass  # duplicate

    print(f"🎉 Loaded {inserted} movies into MongoDB")

if __name__ == "__main__":
    load_movies_from_json()
