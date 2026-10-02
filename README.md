# IBake — Cloud-Ready Spring Boot 3 & React Architecture

Enterprise multi-vendor e-commerce backend for the Indian bakery and gifting market, with production deployment support for **Render** (`render.yaml`, `Dockerfile`) and **Railway** (`railway.json`, `railway.toml`).

## Deployment on Render (render.com)

1. **Deploy with Blueprint**:
   - Push to Git and connect repository on Render Dashboard (`New +` → `Blueprint`).
   - Render reads `render.yaml` automatically.
2. **Deploy as Web Service**:
   - **Environment**: Docker
   - **Dockerfile Path**: `./Dockerfile` (or `backend/Dockerfile.render`)
   - **Default Port**: `10000` (mapped via dynamic `$PORT`)
   - **Healthcheck**: `/actuator/health`

### Environment Variables
- `PORT`: `10000`
- `SPRING_DATASOURCE_URL`: `jdbc:postgresql://<SUPABASE-HOST>:6543/postgres?sslmode=require`
- `SPRING_DATASOURCE_USERNAME`: `<DB_USERNAME>`
- `SPRING_DATASOURCE_PASSWORD`: `<DB_PASSWORD>`
- `JWT_SECRET`: 256-bit+ HMAC SHA key
- `CORS_ALLOWED_ORIGINS`: Frontend URL(s)
