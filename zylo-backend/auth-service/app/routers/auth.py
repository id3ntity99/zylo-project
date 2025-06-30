from fastapi import APIRouter, status


router = APIRouter(prefix="/auth", tags=["auth"])

@router.get("/health", status_code=status.HTTP_200_OK)
def health() -> dict:
    return {"status": "healthy"}
