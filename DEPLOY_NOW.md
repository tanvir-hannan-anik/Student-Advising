# 🚀 QUICK DEPLOY GUIDE - Student Advising Portal

**Follow these steps EXACTLY in order.**

---

## **STEP 1: Clean Deploy on Railway (5 minutes)**

### 1. Delete old broken deployment
- Go to: https://railway.app
- Click your project: `Student-Advising`
- Click the app service
- Click **Settings** → **Danger Zone**
- Click **Delete Service**

### 2. Deploy fresh from GitHub
- Click **+ New Service**
- Select **GitHub Repo**
- Search: `Student-Advising`
- Click **Deploy**
- ⏳ **Wait 5-10 minutes** for build

### 3. Check the logs
In **Deployments** tab, watch for:
```
✓ Build succeeded
✓ Image built successfully
✓ Container starting
✓ Listening on port 8080
```

---

## **STEP 2: Add PostgreSQL Database (2 minutes)**

### 1. Add database service
- In your Railway project dashboard
- Click **+ New Service**
- Select **Database** → **PostgreSQL**
- ⏳ Wait 30 seconds for initialization

### 2. Verify DATABASE_URL
- Go to **Variables** tab
- Should see: `DATABASE_URL=postgresql://...`
- ✓ If present, you're good!

---

## **STEP 3: Set Environment Variables (1 minute)**

In **Variables** tab, add:

```
GROQ_API_KEY = [your_key_from_groq.com]
```

Then click **Redeploy**

---

## **STEP 4: Test Your App (2 minutes)**

### Get your URL
- Go to **Deployments**
- See green URL: `https://student-advising-xxxxx.up.railway.app`

### Test in browser
```
https://your-url.up.railway.app
```

### Test API endpoint
```bash
curl https://your-url.up.railway.app/api/admin-systems/overview
```

Should return JSON data ✓

---

## **If Build Fails:**

Check logs for errors:
1. **Deployments** tab → click failed build
2. Read error message
3. Common issues:
   - `Connection refused` → PostgreSQL not ready yet (wait 2 min)
   - `Out of memory` → Railway re-building (wait)
   - `Port already in use` → Delete old service first

---

## **✅ Success Indicators**

Your app is working when you see:
- ✓ Green deployment status in Railway
- ✓ URL is accessible
- ✓ API endpoint returns JSON
- ✓ Web UI loads

---

## **📊 What's Deployed**

- **Java Spring Boot** app (port 8080)
- **PostgreSQL** database (persistent data)
- **H2 Console** available at `/h2-console`
- **HTTPS** enabled by Railway

---

## **🔧 After Deployment**

You can now:
- Add student records via web UI
- Use AI features (requires GROQ_API_KEY)
- Access data from anywhere
- Data persists permanently

---

## **❓ Need Help?**

Check these in this order:
1. Railway **Deployments** logs
2. Make sure PostgreSQL service exists
3. Verify `GROQ_API_KEY` is set
4. Wait 10+ minutes (first deployment is slow)

---

**That's it! Your app should be live now.** 🎉

