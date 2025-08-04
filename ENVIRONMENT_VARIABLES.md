# Production Environment Variables for Todo AI App

## Required Environment Variables

### Database Configuration
DATABASE_URL=postgresql://username:password@hostname:port/database_name
DATABASE_USERNAME=your_db_username
DATABASE_PASSWORD=your_db_password

### Security Configuration
JWT_SECRET=your_secure_64_character_jwt_secret_key_here

### CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,https://your-frontend.vercel.app

### AI Integration (Optional)
OPENAI_API_KEY=sk-your-openai-api-key-here

### Server Configuration
PORT=8080

## How to Generate Secure Values

### JWT Secret (Required)
Generate a secure 64+ character secret:
```bash
# Using OpenSSL
openssl rand -hex 64

# Using Node.js
node -e "console.log(require('crypto').randomBytes(64).toString('hex'))"

# Using Python
python -c "import secrets; print(secrets.token_hex(64))"
```

### Example Production .env File
```env
DATABASE_URL=postgresql://todouser:securepass123@db.render.com:5432/todoai_prod
DATABASE_USERNAME=todouser
DATABASE_PASSWORD=securepass123
JWT_SECRET=a0b1c2d3e4f5g6h7i8j9k0l1m2n3o4p5q6r7s8t9u0v1w2x3y4z5a6b7c8d9e0f1g2h3
OPENAI_API_KEY=sk-proj-abc123def456ghi789jkl012mno345pqr678stu901vwx234yzab567cde890
PORT=8080
```

## Render Deployment Environment Variables

When deploying to Render, set these in your service dashboard:

1. **DATABASE_URL** - Automatically provided by Render PostgreSQL addon
2. **JWT_SECRET** - Generate and add manually (keep secure!)
3. **OPENAI_API_KEY** - Add your OpenAI API key (optional)
4. **PORT** - Usually auto-set by Render (defaults to 8080)

## Security Notes

- **Never commit secrets to version control**
- **Use different secrets for dev/staging/production**
- **Rotate JWT secrets periodically**
- **Keep OpenAI API key secure and monitor usage**
- **Use strong database passwords**

## Local Development Override

Create a `.env.local` file (add to .gitignore):
```env
DATABASE_URL=jdbc:postgresql://localhost:5432/todoai_dev
DATABASE_USERNAME=dev_user
DATABASE_PASSWORD=dev_password
JWT_SECRET=dev_secret_key_not_for_production_use_only_64_chars_minimum
OPENAI_API_KEY=your_dev_openai_key
```
