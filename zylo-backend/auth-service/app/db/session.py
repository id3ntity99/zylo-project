from typing import Generator
from sqlalchemy import create_engine
from sqlmodel import Session

from app.core.config import get_settings

settings = get_settings()
engine = create_engine(
    settings.DATABASE_URL,
    echo=settings.profile == "dev",
    pool_pre_ping=True,
    pool_size=10,
    max_overflow=20,
)

def get_session() -> Generator[Session, None, None]:
    with Session(engine) as session:
        yield session
