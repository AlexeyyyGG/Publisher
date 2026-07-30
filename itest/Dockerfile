FROM gradle:8.7-jdk17
WORKDIR /app
COPY . .
CMD ["gradle", ":itest:test", "--no-daemon"]