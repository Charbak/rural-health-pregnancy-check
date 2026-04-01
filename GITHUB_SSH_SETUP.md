# GitHub SSH Key Setup Guide

This guide will help you generate SSH keys and add them to GitHub so you can push your ANC Rural Health project.

## Step 1: Check for Existing SSH Keys

Open PowerShell or Command Prompt and run:

```bash
ls ~/.ssh
```

If you see files like `id_rsa` and `id_rsa.pub` or `id_ed25519` and `id_ed25519.pub`, you already have SSH keys.

## Step 2: Generate New SSH Key

### Option A: Using Git Bash (Recommended)

1. **Install Git for Windows** (if not already installed):
   - Download from: https://git-scm.com/download/win
   - Run installer with default settings

2. **Open Git Bash** (search for "Git Bash" in Windows)

3. **Generate SSH key**:
   ```bash
   ssh-keygen -t ed25519 -C "your_email@example.com"
   ```
   
   Replace `your_email@example.com` with your GitHub email address.

4. **When prompted**:
   - "Enter file in which to save the key": Press **Enter** (uses default location)
   - "Enter passphrase": Press **Enter** for no passphrase, or type a secure passphrase
   - "Enter same passphrase again": Press **Enter** or retype passphrase

5. **Your SSH key is now generated!**
   - Private key: `~/.ssh/id_ed25519`
   - Public key: `~/.ssh/id_ed25519.pub`

### Option B: Using PowerShell

```powershell
ssh-keygen -t ed25519 -C "your_email@example.com"
```

Follow the same prompts as above.

### Option C: Using Command Prompt

```cmd
ssh-keygen -t ed25519 -C "your_email@example.com"
```

## Step 3: Start SSH Agent

### Git Bash:
```bash
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_ed25519
```

### PowerShell (Run as Administrator):
```powershell
# Start the service
Get-Service -Name ssh-agent | Set-Service -StartupType Manual
Start-Service ssh-agent

# Add your SSH key
ssh-add $env:USERPROFILE\.ssh\id_ed25519
```

## Step 4: Copy Your Public SSH Key

### Method 1: Using Command (Easiest)

**Git Bash or PowerShell:**
```bash
cat ~/.ssh/id_ed25519.pub | clip
```

This copies your public key to clipboard.

### Method 2: Manual Copy

1. Open the file in Notepad:
   ```bash
   notepad ~/.ssh/id_ed25519.pub
   ```

2. Copy all the text (Ctrl+A, Ctrl+C)

The key looks like:
```
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIJl3dIeudNqd0DPMRD6OIh65A9pu9yBW your_email@example.com
```

## Step 5: Add SSH Key to GitHub

1. **Go to GitHub**: https://github.com

2. **Click your profile picture** (top right) → **Settings**

3. **In the left sidebar**, click **SSH and GPG keys**

4. **Click "New SSH key"** (green button)

5. **Fill in the form**:
   - **Title**: Give it a name (e.g., "My Windows PC" or "Work Laptop")
   - **Key type**: Authentication Key
   - **Key**: Paste your public key (from clipboard)

6. **Click "Add SSH key"**

7. **Confirm with your GitHub password** if prompted

## Step 6: Test Your SSH Connection

```bash
ssh -T git@github.com
```

You should see:
```
Hi username! You've successfully authenticated, but GitHub does not provide shell access.
```

If you see this message, **you're all set!** ✅

## Step 7: Configure Git (First Time Only)

```bash
git config --global user.name "Your Name"
git config --global user.email "your_email@example.com"
```

## Step 8: Push Your Project to GitHub

### Create a New Repository on GitHub

1. Go to https://github.com/new
2. Repository name: `anc-rural-health`
3. Description: "ANC tracking app for rural India"
4. Choose **Public** or **Private**
5. **Do NOT** initialize with README (we already have files)
6. Click **Create repository**

### Push Your Code

In your project directory:

```bash
cd "c:\Users\CHARBAKROY\Desktop\Mobile App"

# Initialize git (if not already done)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit: ANC Rural Health Android App"

# Add remote (replace YOUR_USERNAME with your GitHub username)
git remote add origin git@github.com:YOUR_USERNAME/anc-rural-health.git

# Push to GitHub
git branch -M main
git push -u origin main
```

## Troubleshooting

### "Permission denied (publickey)"
- Make sure you copied the **public** key (`.pub` file)
- Verify the key is added to GitHub
- Check SSH agent is running: `ssh-add -l`

### "Could not open a connection to your authentication agent"
- Start SSH agent:
  ```bash
  eval "$(ssh-agent -s)"
  ssh-add ~/.ssh/id_ed25519
  ```

### "ssh-keygen is not recognized"
- Install Git for Windows: https://git-scm.com/download/win
- Use Git Bash instead of Command Prompt

### Key already exists
- Use existing key, or
- Generate with different name:
  ```bash
  ssh-keygen -t ed25519 -C "your_email@example.com" -f ~/.ssh/id_ed25519_github
  ```

## Alternative: Use HTTPS Instead of SSH

If SSH is too complicated, you can use HTTPS:

```bash
# Use HTTPS URL instead
git remote add origin https://github.com/YOUR_USERNAME/anc-rural-health.git
git push -u origin main
```

You'll need to enter your GitHub username and **Personal Access Token** (not password).

### Create Personal Access Token:
1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token
3. Select scopes: `repo` (full control)
4. Copy token and use it as password when pushing

## Quick Reference

```bash
# Generate SSH key
ssh-keygen -t ed25519 -C "your_email@example.com"

# Copy public key to clipboard
cat ~/.ssh/id_ed25519.pub | clip

# Test connection
ssh -T git@github.com

# Push to GitHub
git remote add origin git@github.com:YOUR_USERNAME/anc-rural-health.git
git push -u origin main
```

## Next Steps After Pushing to GitHub

1. Go to your repository on GitHub
2. Click **Actions** tab
3. Set up the build workflow from QUICK_START.md
4. GitHub will automatically build your APK!

## Need Help?

- GitHub SSH Documentation: https://docs.github.com/en/authentication/connecting-to-github-with-ssh
- Git for Windows: https://git-scm.com/download/win
- GitHub Support: https://support.github.com/

---

**Security Note**: Never share your **private key** (`id_ed25519` without `.pub`). Only share the **public key** (`id_ed25519.pub`).