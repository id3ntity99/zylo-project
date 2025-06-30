from fastapi import APIRouter,status


router = APIRouter(prefix="/user", tags=["user"])

@router.get("/health", status_code=status.HTTP_200_OK)
def health_check() -> dict:
    return {"status": "healthy"}
