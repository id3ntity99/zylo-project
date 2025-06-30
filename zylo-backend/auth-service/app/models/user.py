from sqlalchemy import Column, Index, String
from sqlmodel import Field, SQLModel


class User(SQLModel, table=True):
    __tablename__ = "user"
    __table_args__ = ( # 테이블 속성
        Index("id_UNIQUE", "id", unique=True),
        Index("email_UNIQUE", "email", unique=True),
        {"mysql_charset": "utf8mb4"},
    )

    id: str = Field(primary_key=True, max_length=16, default=None)

    password: str = Field(max_length=255, nullable=False)

    email: str = Field(max_length=255, nullable=False)

    name: str = Field(max_length=40, nullable=False)

    dept: str = Field(max_length=50, nullable=False)
