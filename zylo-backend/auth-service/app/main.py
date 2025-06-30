from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from sqlmodel import SQLModel

from app.routers import auth, user
from app.db.session import engine

from .core.config import get_settings

settings = get_settings()

@asynccontextmanager
async def lifespan(app: FastAPI): # FastAPI 앱 생명주기(실행 및 종료)에 관한 로직
    if settings.profile == "dev":
        print("Creating DB tables if not exist...") 
    SQLModel.metadata.create_all(bind=engine)
    yield
    if settings.profile == "dev":
        print("Application shutdown complete")

app = FastAPI( # FastAPI 초기화
    title = "zylo auth server test",
    version= "0.0.1",
    lifespan=lifespan,
    debug=settings.profile == "dev",
    docs_url="/docs",
    redoc_url=None,
    openapi_tags = []  
)

app.add_middleware( # CORS 정책 관련 미들웨어 초기화
    CORSMiddleware,
    allow_origins=["*"] if settings.profile == "dev" else ["https://greenlotteon.com"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router)
app.include_router(user.router)

if __name__ == "__main__":
    import uvicorn
    
    uvicorn.run(
        "app.main:app",
        host="localhost",
        port=8000,
        reload=settings.profile == "dev",
        log_level="info",
    )