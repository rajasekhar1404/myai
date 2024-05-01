## AI Local Environment Setup in Termux
- Download and Install Termux from F-Droid: https://f-droid.org/en/packages/com.termux/
- Open Termux and update all apps: `apt update && apt upgrade`
- Setting up Root user access (Needed for running any scripts)
  - Install git: `pkg install git -y`
  - Close root-termux repo and give all permissions: `git clone https://github.com/hctilg/root-termux.git && cd root-termux && chmod +x *`
  - Install wget and proot: `pkg install wget proot -y`
  - Run bash script to install root-access: `yes | bash install.sh`
  - start the root environment: `bash start.sh`
  - Need to update and upgrade after setting root-user: `apt update && apt upgrade`
- Setup Ollama
  - Install curl for downloading Ollama: `apt install curl`
  - Install ollama: `curl -fsSL https://ollama.com/install.sh | sh`
  - Run LLM (using phi3 in this example, you can choose any open-source llm here): `ollama run phi3`
  - Once the llm is downloaded, you can start asking the questions.
- Setup ChromaDB
  - Install python for downloading ChromaDB: `apt install python3`
  - Install pip for downloading python libraries: `apt install python3-pip`
  - Install ChromaDB: `pip install chromadb`
  - Check if chroma is starting: `chroma run`, probably it will fail to start with error: `Name or service not know`
  - Skip below steps if chroma starts successfully, otherwise this error is due to the missing hostname in /etc/hosts, so lets append it.
  - Install sudo: `apt install sudo`
  - Add localhost to /etc/hosts: `echo "127.0.0.1 $HOSTNAME" | sudo tee -a /etc/hosts`
  - Now Run chroma: `chroma run`
- That's all, now you are ready to build AI applications in your mobile.

## Java setup
- Download jdk-17: `apt install openjdk-17-jdk openjdk-17-jre`
- Download maven: `apt install maven`
- Run spring-boot application: `mvn spring-boot:run`

## React JS setup
- Download npm: `apt install npm`
- Start react js application: `npm start`