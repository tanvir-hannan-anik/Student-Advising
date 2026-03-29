# Deployment Guide - Student Advising Portal

Your application is ready to deploy! Choose your deployment method below.

---

## **🚀 Option 1: Deploy to Railway.app (RECOMMENDED - Full Stack)**

Railway.app supports both Java and Python in one project. This is the best option.

### **Quick Start (5 minutes)**

1. **Go to**: https://railway.app
2. **Sign up with GitHub** (authorize access to your repositories)
3. **Click**: "New Project" → "Deploy from GitHub repo"
4. **Select**: `tanvir-hannan-anik/Student-Advising`
5. **Click**: "Deploy"

Railway will automatically build and deploy your application using the Dockerfile!

### **After Deployment**

1. Your app will get a public URL (e.g., `https://student-advising.up.railway.app`)
2. Go to **Variables** tab and add (optional):
   ```
   GROQ_API_KEY=your_key_here
   GMAIL_USER=your_email@gmail.com
   GMAIL_APP_PASSWORD=your_app_password
   ```
3. **Redeploy** and your app is live!

### **Access Your App**

```
Web UI:     https://student-advising.up.railway.app
API:        https://student-advising.up.railway.app/api/admin-systems/overview
AI Service: https://student-advising.up.railway.app/ai/health
```

---

## **⚡ Option 2: Deploy to Heroku (Alternative)**

If you have Heroku account:

```bash
# Install Heroku CLI
# Login to Heroku
heroku login

# Create app
heroku create your-app-name

# Deploy
git push heroku main

# View logs
heroku logs --tail
```

---

## **🐳 Option 3: Deploy with Docker Locally (Testing)**

Test deployment locally before pushing to cloud:

```bash
# Build Docker image
docker build -t student-advising .

# Run container
docker run -p 8080:8080 -p 5000:5000 student-advising

# Access
# http://localhost:8080
```

---

## **📊 Optional: Add PostgreSQL Database**

For production, add a PostgreSQL database instead of H2:

### **Railway.app Steps:**

1. In your Railway project, click **"+ New Service"**
2. Select **"Database"** → **"PostgreSQL"**
3. Railway auto-creates connection URL
4. Set environment variable:
   ```
   DATABASE_URL=postgresql://user:password@host:port/dbname
   ```
5. Redeploy

---

## **🔧 Environment Variables Needed**

| Variable | Required | Description |
|----------|----------|-------------|
| `GROQ_API_KEY` | ✅ Yes | API key from https://groq.com |
| `GMAIL_USER` | ❌ Optional | Gmail for sending emails |
| `GMAIL_APP_PASSWORD` | ❌ Optional | Gmail app-specific password |
| `DATABASE_URL` | ❌ Optional | PostgreSQL URL (if using cloud DB) |
| `PORT` | ❌ Auto | (Railway sets this automatically) |

---

## **✅ Verify Deployment**

Once deployed, test these endpoints:

```bash
# Check if app is running
curl https://your-app-url/api/admin-systems/overview

# Check AI service
curl https://your-app-url/ai/health

# Access web UI
open https://your-app-url
```

---

## **🐛 Troubleshooting**

### **App won't start**
- Check environment variables are set
- View deployment logs in Railway dashboard
- Ensure GROQ_API_KEY is valid

### **Slow first load**
- First load downloads 300MB+ dependencies
- This is normal and takes 2-5 minutes
- Subsequent loads are fast

### **Database not persisting**
- Add PostgreSQL service (see above)
- H2 file-based DB doesn't persist in Heroku/Railway

---

## **📱 Local Testing (Before Deploying)**

Your app is already running locally:

```
Web UI:     http://localhost:8080
AI Service: http://localhost:5000
```

To restart locally:

```bash
# Terminal 1 - Python AI service
cd ai-service
python app.py

# Terminal 2 - Java app
mvn spring-boot:run
```

---

## **💡 Next Steps**

1. ✅ Get GROQ_API_KEY from https://groq.com (free)
2. ✅ Deploy to Railway.app (2 minutes)
3. ✅ Set environment variables
4. ✅ Test your live app

**Need help?** Check Railway.app docs: https://docs.railway.app

