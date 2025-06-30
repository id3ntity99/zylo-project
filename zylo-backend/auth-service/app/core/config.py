from functools import lru_cache
import os

from pathlib import Path
from pydantic_settings import BaseSettings 

BASE_DIR = Path(__file__).resolve().parent.parent.parent

class Settings(BaseSettings):
    ACCESS_TOKEN_EXPIRE_MINUTES: int
    ALGORITHM: str
    SECRET_KEY: str
    DATABASE_URL: str
    TOKEN_ISSUER: str

    _env: dict | None = None
    _profile: str | None = None

    def __init__(self, profile:str | None = None):
        profile = profile or "prod"
        env_path = BASE_DIR / f".env-{profile}"
        if not env_path.exists():
            env_path = BASE_DIR / ".env-prod"

        super().__init__(_env_file=env_path)

        self._profile = profile

    @property
    def profile(self) -> str:
        return self._profile

@lru_cache # 싱글톤
def get_settings() -> Settings:
    return Settings(profile=os.getenv("PROFILE"))
