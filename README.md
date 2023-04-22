# devops
Niv Itzhaky<br>
https://www.linkedin.com/in/nivitzhaky/<br>
niv.itzhaky@gmail.com<br>
0525236451<br>

link to presentation -> https://docs.google.com/presentation/d/1fb-fyxiP5T-3Xdn1d7XT1kwAY2jOSOCy1TY5jITSOtw/edit?usp=sharing <br>
create your own account <br>
## START
fork https://github.com/handson-academy/ops-basic-spring/ to your own git <br>

### EC2
create ec2 instance: <br>
name->testec2, new keypair-> test,  launch instance <br>
go to inboud rules-> allow all traffic


### DOCKER
```
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo curl -L https://github.com/docker/compose/releases/download/1.22.0/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

sudo docker run -d \
    -e MYSQL_ROOT_PASSWORD=Unix11 \
    -e MYSQL_DATABASE=students \
    -e MYSQL_USER=students \
    -e MYSQL_PASSWORD=Unix11 \
    -p 3306:3306 \
    -v mysql-data:/var/lib/mysql \
    mysql:8.0
    
sudo docker ps
sudo docker logs [containerid]

sudo docker kill [containerid]
```

### GIT
attach ssh key to git
```
ssh-keygen
cat ./.ssh/id_rsa.pub
```

point to local db <br>
change src/main/resources/application.properties<br>
```
spring.datasource.url=jdbc:mysql://mysql:3306/students
spring.datasource.username=students
spring.datasource.password=Unix11
```

install git
```
sudo yum update
sudo yum install git
git clone git@github.com:nivitzhaky/ops-basic-spring.git 
```
### BASIC Linux commands
```
fork repo: https://github.com/handson-academy/basic-html to your account 
git clone https://github.com/nivitzhaky/basic-html.git
cd basic-html/
git branch test-branch

```



### MAVEN
install maven
```
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven

cd ops-basic-spring
mvn clean install
```
there is a new folder called target with basic-0.0.1-SNAPSHOT.jar file
### CREATE DOCKERHUB USER
https://dockerhub.com <br>
create token account setting -> security

### DOCKERIZE
```
sudo docker build . -t backend
sudo docker login
nivitzhaky
Jul201789#
sudo docker tag backend nivitzhaky/backend
sudo docker push nivitzhaky/backend

git pull
echo "
version: \"3\"
services:
  appserver:
    container_name: server
    hostname: localhost
    image: nivitzhaky/backend:latest
    ports:
      - "8080:8080"
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: Unix11
      MYSQL_DATABASE: students
      MYSQL_USER: students
      MYSQL_PASSWORD: Unix11
    ports:
      - "3306:3306"
    volumes:
      - ./mysql-data:/var/lib/mysql
    privileged: true
" >>  docker-compose.yml

sudo  docker-compose   up -d
# test http://[ip address]:8080/swagger-ui.html
sudo docker-compose   down
```
### TEST FRONTEND
https://github.com/handson-academy/ops-basic-angular
```
cd ~
git clone git@github.com:handson-academy/ops-basic-angular.git
cd ops-basic-angular

echo "
export const environment = {
  production: true,
  url: 'http://13.50.247.173:8080/api' 
};
" > src/environments/environment.prod.ts

echo "
export const environment = {
  production: false,
  url: 'http://13.50.247.173:8080/api' 
};
" > src/environments/environment.ts


sudo docker run -p 3000:3000 -v $(pwd):/app  -d node:14 tail -f /dev/null
sudo docker ps
sudo docker exec -it [containerid]  /bin/bash

cd /app
npm install
npm run build --prod


npm install -g http-server
nohup http-server /app/dist/webapp -p 3000 &

```
test on http://13.50.247.173:3000

### DOCKER AUTOMATION
add the following secrets:
```
DOCKERHUB_USERNAME = nivitzhaky
DOCKERHUB_TOKEN = dckr_pat_wNsuA4lJiuBnc4iCsNCmxjCVjc4
EC2_INSTANCE_PUBLIC_IP = 13.50.235.108
SSH_KEY = 

```


add .github/workflows/build.yml
```
name: Build and Deploy

on:
  push:
    branches:
      - master

env:
  APP_VERSION: v1.0.${{ github.run_number }}

jobs:
  build:
    name: Build & Deploy
    runs-on: ubuntu-latest
    steps:
      - name: checkout
        uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
          distribution: 'adopt'
      - name: Build and analyze
        env:
          GITHUB_TOKEN: ${{ secrets.ACCESSE_TOKEN }}
        run: mvn clean install
      - name: Set up QEMU
        uses: docker/setup-qemu-action@v1
      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v1
      - name: Login to DockerHub
        uses: docker/login-action@v1
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}
      - name: Build and push
        id: docker_build
        uses: docker/build-push-action@v2
        with:
          context: .
          push: true
          tags: ${{ secrets.DOCKERHUB_USERNAME }}/backend:${{ env.APP_VERSION }}
      - name: Deploy to AWS EC2
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.EC2_INSTANCE_PUBLIC_IP }}
          username: ec2-user
          key: ${{ secrets.SSH_KEY }} # store the SSH password as a secret in the repository
          script: |
            cd /home/ec2-user/ops-basic-spring
            sudo /usr/local/bin/docker-compose  down || true
            sed -i 's/image: ${{ secrets.DOCKERHUB_USERNAME }}\/backend:.*/image: ${{ secrets.DOCKERHUB_USERNAME }}\/backend:v1.0.${{ github.run_number }}/g' /home/ec2-user/ops-basic-spring/docker-compose.yml
            sudo /usr/local/bin/docker-compose  up -d || true
```
change /src/main/java/com/handson/basic/controller/StudentsController.java <br>
getHighSatStudents -> getHighSatStudents1
check that swagger updates

### S3 deploy
create bucket niv.backend.students <br>
public false<br>
in properties make static web hosting true<br>
add this to permissions:
```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::niv.backend.students/*"
        }
    ]
}
```


in AIM create a user for the deploy with admin privileges <br>
create and save the access key
fork the repository: https://github.com/handson-academy/ops-basic-angular
fill the secrets:
```
AWS_ACCESS_KEY_ID: 
AWS_SECRET_ACCESS_KEY:
AWS_REGION: <YOUR_AWS_REGION>
S3_BUCKET_NAME: <YOUR_S3_BUCKET_NAME>
```
change backend url to: 'http://13.50.235.108:8080/api'
in ops-basic-angular/src/environments/environment.ts  
and ops-basic-angular/src/environments/environment-prod.ts 

create the build.yml
```
name: Deploy to S3 Bucket
on:
  push:
    branches:
      - main
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v2
      - name: Install Node.js
        uses: actions/setup-node@v1
        with:
          node-version: '14.x'
      - name: Install dependencies
        run: npm install
      - name: Build application
        run: npm run build --prod
      - name: Install AWS CLI
        run: |
          sudo apt-get update
          sudo apt-get install -y python3-pip
          pip3 install awscli --upgrade --user
      - name: Deploy to S3
        env:
          AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
          AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          AWS_REGION: ${{ secrets.AWS_REGION }}
          S3_BUCKET_NAME: ${{ secrets.S3_BUCKET_NAME }}
        run: |
          aws s3 sync ./dist/webapp s3://${{ env.S3_BUCKET_NAME }} --delete
```

### DOMAIN
1. buy a domain on https://start.godaddy.com/ <br>

in aws go to: route53:
create hosted zone

take the values from here:
https://us-east-1.console.aws.amazon.com/route53/v2/hostedzones#ListRecordSets/Z0578988Z5FGXIZ849XL <br>

to godaddy:
<br>
change nameservers from :
<br>
Nameservers<br>
ns19.domaincontrol.com <br>
ns20.domaincontrol.com <br>
<br>
to: <br>
ns-1255.awsdns-28.org <br>
ns-579.awsdns-08.net  <br>
ns-438.awsdns-54.com  <br>
ns-1717.awsdns-22.co.uk <br>

### Domain for ec2
create A record -> [ec2 ip] -> ec2-raw.nivitzhaky.com


### Cloudfront
XXXX create 


in the project change the url in 
environment.prod.ts and 
environment.ts <br>
to: 'https://ec2-stage.nivitzhaky.com/api'

create distribution<br>
origin1-> select s3 bucket -> use website endpoint -> caching disabled <br>
alternate domain: ec2-stage.nivitzhaky.com -> custom ssl : request certificate<br>
fully qualified name:  nivitzhaky.com , *.nivitzhaky.com <br>
validate with dns <br>
cname (give name) ec2-stage.nivitzhaky.com and copy cloudfront distribution url<br>
hosted zones-> domain -> create record ->
<br><br>
origin2->
http only -> 8080-> origin = ec2-raw.nivitzhaky.com => choose all allowed http methods<br>
alternate domain name-> ec2-stage.nivitzhaky.com<br>
Custom SSL certificate - optional -> nivitzhaky.com<br>
<br>
behaviours: <br>
api/* -> allowed methods all -> caching all <br>
* -> leave default



TERMINATE THE MACHINE IF YOU WANT... <br>


## ECS

### RDS

create a database.
publicly accesible   , master user: admin, masterpassword: Unix11!! <br>
after created goto inboud rules and add "alltrafic"

```

create database students_stage_ecs;
CREATE USER 'students_staging_ecs'@'%' IDENTIFIED BY 'students_staging_ecs';
GRANT all PRIVILEGES on students_stage_ecs to 'students_staging_ecs'@'%';
GRANT all PRIVILEGES on students_stage_ecs.* to 'students_staging_ecs'@'%';

create database students_stage_eks;
CREATE USER 'students_staging_eks'@'%' IDENTIFIED BY 'students_staging_eks';
GRANT all PRIVILEGES on students_stage_eks to 'students_staging_eks'@'%';
GRANT all PRIVILEGES on students_stage_eks.* to 'students_staging_eks'@'%';


```


### AIM
go to AIM and create a user with programatic access and console sign in (admin credentials)
```
accountid = 
access_key=
secret_key=
username=academy
password=Unix11!@
```


### GITLAB 
create account <br>
import https://github.com/handson-academy/ops-basic-spring.git <br>
create branch ecs

### GITLAB variables


on gitlab, backend service. <br>
goto: settings -> ci/cd <br>
expand variables, update: (unprotect the vars)
```
AWS_ACCESS_KEY_ID=
AWS_DEFAULT_REGION=eu-north-1
AWS_SECRET_ACCESS_KEY=
CI_AWS_ECS_CLUSTER=ecs-stage-cluster
CI_AWS_ECS_SERVICE=ecs-stage-service
```


### Parameter store
```
students_staging_ecs=jdbc:mysql://database-2.cmyyngkp9f7o.eu-north-1.rds.amazonaws.com:3306/students_stage_ecs
students_staging_ecs_user=students_staging_ecs
students_staging_ecs_password=students_staging_ecs
students_staging_eks=jdbc:mysql://database-2.cmyyngkp9f7o.eu-north-1.rds.amazonaws.com:3306/students_stage_eks
students_staging_eks_user=students_staging_eks
students_staging_eks_password=students_staging_eks
```
### ECR
create an ECR, call it students-ecs

### automation files
ci-settings.xml
```
<settings xmlns="http://maven.apache.org/SETTINGS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd">
  <servers>
    <server>
      <id>gitlab-maven</id>
      <configuration>
        <httpHeaders>
          <property>
            <name>Job-Token</name>
            <value>${CI_JOB_TOKEN}</value>
          </property>
        </httpHeaders>
      </configuration>
    </server>
  </servers>
</settings>
```
.gitlab-ci.yml
```
variables:
  DOCKER_REGISTRY: [ecr url - no /]
  AWS_DEFAULT_REGION: eu-north-1
  APP_NAME: students-ecs
  DOCKER_HOST: tcp://docker:2375

publish:
  image: 
    name: maven:3.8.1-openjdk-11
    entrypoint: [""]
  services:
    - docker:dind
  before_script:
    - apt-get update
    - apt-get install -y python3-pip
    - pip3 install awscli
    - aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
    - aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
    - aws configure set region $AWS_DEFAULT_REGION
    - export DB_URL=$(aws ssm get-parameter --name "students_staging_ecs" --query "Parameter.Value" --output text)
    - echo $DB_URL
    - export DB_PASSWORD=$(aws ssm get-parameter --name "students_staging_ecs_password" --query "Parameter.Value" --output text)
    - echo $DB_PASSWORD
    - export DB_USER=$(aws ssm get-parameter --name "students_staging_ecs_user" --query "Parameter.Value" --output text)
    - echo $DB_USER
    - sed -i "s#spring.datasource.url=.*#spring.datasource.url=${DB_URL}#g"  src/main/resources/application.properties
    - sed -i "s#spring.datasource.username=.*#spring.datasource.username=${DB_USER}#g"  src/main/resources/application.properties
    - sed -i "s#spring.datasource.password=.*#spring.datasource.password=${DB_PASSWORD}#g"  src/main/resources/application.properties
    - cat src/main/resources/application.properties
    - apt-get update
    - apt-get install -y curl
    - curl -fsSL https://get.docker.com | sh
    - aws --version
    - docker --version
    - mvn --version
  script:
    - mvn clean install
    - aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $DOCKER_REGISTRY
    - docker build -t $DOCKER_REGISTRY/$APP_NAME:latest . 
    - docker push $DOCKER_REGISTRY/$APP_NAME:latest  


```


### ECS
create taskdefinition ->  ecs-task-definition <br>
containername ->student-ecs-container<br>
imageuri-> copy from ecr table<br>
container port-> 8080 <br>
cpu - 1, memory- 4 <br>
configure healthcheck:  CMD-SHELL, curl -f http://localhost:8080/actuator/health <br>


create cluster: ecs-stage-cluster <br>
create a service: <br>
fargate-> service-> family=ecs-task-definition->servicename = ecs-stage-service <br>


load balancing -> create applicaiton load balancer -> springboot-lb -> port 8080 <br>
create a target group->  students-ecs-tg-> healthcheck:  /actuator/health -> grace 120 sec <br>


when the task goes up we can test by ip:8080/swagger-ui.html

### ADD ECS DEPLOY to GITLAB
add to .gitlab-ci.yml 
```
deploy:
  image: 
    name: docker:19.03.10
    entrypoint: [""]
  services:
    - docker:dind
  before_script:
    - apk add --no-cache curl jq python py-pip
    - pip install awscli
    - aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
    - aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
    - aws configure set region $AWS_DEFAULT_REGION
    - aws ecr get-login-password --region eu-north-1 | docker login --username AWS --password-stdin $DOCKER_REGISTRY
  stage: deploy
  script:
    - echo $REPOSITORY_URL:$IMAGE_TAG 
    - echo "Updating the service..."
    - aws ecs update-service --region "${AWS_DEFAULT_REGION}" --cluster "${CI_AWS_ECS_CLUSTER}" --service "${CI_AWS_ECS_SERVICE}"  --force-new-deployment

```

### front repository:
gitlab import https://github.com/handson-academy/ops-basic-angular.git <br>
create branch ecs <br>
create a public s3 bucket: ecs-stage.files.nivitzhaky.com   (enable static web hosting)
in permissions:
```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicRead",
            "Effect": "Allow",
            "Principal": "*",
            "Action": [
                "s3:GetObject",
                "s3:GetObjectVersion"
            ],
            "Resource": "arn:aws:s3:::ecs-stage.files.nivitzhaky.com/*"
        }
    ]
}
```

### GITLAB front:
env variable 
```
BACKEND_URL_ECS -> https:\/\/ecs-stage.nivitzhaky.com\/api
AWS_ACCESS_KEY_ID 
AWS_SECRET_ACCESS_KEY 
```

ci-settings.xml
```
<settings xmlns="http://maven.apache.org/SETTINGS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd">
  <servers>
    <server>
      <id>gitlab-maven</id>
      <configuration>
        <httpHeaders>
          <property>
            <name>Job-Token</name>
            <value>${CI_JOB_TOKEN}</value>
          </property>
        </httpHeaders>
      </configuration>
    </server>
  </servers>
</settings>
```

.gitlab-ci.yml
```
build stage:  
   image: doctrine/nodejs-aws-cli:v10.19
   stage: build  
   only:    
      - ecs  
   script:
      - echo $BACKEND_URL_ECS
      - sed -i "s/backend_url/'$BACKEND_URL_ECS'/" src/environments/environment.ts
      - sed -i "s/backend_url/'$BACKEND_URL_ECS'/" src/environments/environment.prod.ts    
      - cat src/environments/environment.ts
      - cat src/environments/environment.prod.ts
      - npm install --save --legacy-peer-deps    
      # Build App    
      - npm run build 
   artifacts:    
      paths:      
         # Build folder      
         - dist/    
      expire_in: 1 hour

deploy stage:  
   image: python:latest  
   stage: deploy  
   only:    
      - ecs  
   script:    
      - pip install awscli    
      - aws s3 sync ./dist/webapp/ s3://ecs-stage.files.nivitzhaky.com   
```
### Cloudfront
create distribution<br>
origin1-> select s3 bucket -> use website endpoint -> caching disabled <br>
alternate domain: ecs.nivitzhaky.com
cname (give name) ecs.nivitzhaky.com and copy cloudfront distribution url<br>
hosted zones-> domain -> create record ->
<br><br>
origin2->
http only ->  springboot-lb -> 8080->  choose all allowed http methods<br>
alternate domain name-> ecs.nivitzhaky.com
<br>
behaviours: <br>
api/* -> allowed methods all -> caching all <br>
* -> leave default

cname (give name) ecs.nivitzhaky.com and copy cloudfront distribution url



## EKS
https://eksworkshop.com
### Cloud 9 
create a cloud 9 computer call it students

### INSTALL KUBERNETES

kubectl install
```
sudo curl --silent --location -o /usr/local/bin/kubectl \
   https://s3.us-west-2.amazonaws.com/amazon-eks/1.21.5/2022-01-21/bin/linux/amd64/kubectl

sudo chmod +x /usr/local/bin/kubectl
```

update aws cli
```
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```
install jq
```
sudo yum -y install jq gettext bash-completion moreutils
echo 'yq() {
  docker run --rm -i -v "${PWD}":/workdir mikefarah/yq "$@"
}' | tee -a ~/.bashrc && source ~/.bashrc
```
kubectl bash completion
```
kubectl completion bash >>  ~/.bash_completion
. /etc/profile.d/bash_completion.sh
. ~/.bash_completion
```

verify commands are in path:
```
for command in kubectl jq envsubst aws
  do
    which $command &>/dev/null && echo "$command in path" || echo "$command NOT FOUND"
  done
```
aws load balancer control version
```
echo 'export LBC_VERSION="v2.4.1"' >>  ~/.bash_profile
echo 'export LBC_CHART_VERSION="1.4.1"' >>  ~/.bash_profile
.  ~/.bash_profile
```

create role for the computer: <br>
https://console.aws.amazon.com/iam/home#/roles$new?step=review&commonUseCase=EC2%2BEC2&selectedUseCase=EC2&policies=arn:aws:iam::aws:policy%2FAdministratorAccess&roleName=eks-admin
<br>
next next<br>
turn off credential managment in settings icone -> aws settings -> turn off temporary credentials <br>
go to top right -> manage ec2 instance -> actions -> security -> modify aim role -> select eks-admin


security
```
aws kms create-alias --alias-name alias/eks --target-key-id $(aws kms create-key --query KeyMetadata.Arn --output text)
export MASTER_ARN=$(aws kms describe-key --key-id alias/eks --query KeyMetadata.Arn --output text)
echo "export MASTER_ARN=${MASTER_ARN}" | tee -a ~/.bash_profile
```

### install eks-ctl
```

curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp

sudo mv -v /tmp/eksctl /usr/local/bin

eksctl version

eksctl completion bash >> ~/.bash_completion
. /etc/profile.d/bash_completion.sh
. ~/.bash_completion

export ACCOUNT_ID=$(aws sts get-caller-identity --output text --query Account)
export AWS_REGION=$(curl -s 169.254.169.254/latest/dynamic/instance-identity/document | jq -r '.region')
export AZS=($(aws ec2 describe-availability-zones --query 'AvailabilityZones[].ZoneName' --output text --region $AWS_REGION))

test -n "$AWS_REGION" && echo AWS_REGION is "$AWS_REGION" || echo AWS_REGION is not set

echo "export ACCOUNT_ID=${ACCOUNT_ID}" | tee -a ~/.bash_profile
echo "export AWS_REGION=${AWS_REGION}" | tee -a ~/.bash_profile
echo "export AZS=(${AZS[@]})" | tee -a ~/.bash_profile
aws configure set default.region ${AWS_REGION}
aws configure get default.region

cat << EOF > eks.yaml
---
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig

metadata:
  name: eks-students
  region: ${AWS_REGION}
  version: "1.25"

availabilityZones: ["${AZS[0]}", "${AZS[1]}"]

managedNodeGroups:
- name: nodegroup
  desiredCapacity: 2
  instanceType: t3.medium
  ssh:
    enableSsm: true

# To enable all of the control plane logs, uncomment below:
# cloudWatch:
#  clusterLogging:
#    enableTypes: ["*"]

secretsEncryption:
  keyARN: ${MASTER_ARN}
EOF


eksctl create cluster -f eks.yaml
```


### MANUAL TEST
```
git clone https://github.com/handson-academy/ops-basic-spring

manually copy the templates and replace values
deployment.yaml
image from ecr

service.yaml:
service:
  type: LoadBalancer
  port: 8081


kubectl apply -f deployment.yaml 
kubectl get deployment
kubectl get pods

kubectl apply -f service.yaml
kubectl get service
kubectl logs [podid]
kubectl exec -it [podid] -- /bin/bash
kubectl describe service springboot-service
kubectl get service springboot-service -o yaml
kubectl scale deployment   springboot-deployment   --replicas=1
kubectl delete -f deployment.yaml 
kubectl delete -f service.yaml 
```

### HELM TEST
```
rm -rf ops-basic-spring
git clone https://github.com/handson-academy/ops-basic-spring

curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 > get_helm.sh
chmod 700 get_helm.sh
./get_helm.sh

update ecr in values.yaml

helm upgrade -i springboot springboot/ --values springboot/values.yaml
helm list

go to https://artifacthub.io/packages/helm/groundhog2k/postgres

helm repo add groundhog2k  https://groundhog2k.github.io/helm-charts/
helm repo list
helm search repo postgres
helm show values groundhog2k/postgres > postgres-values.yaml
helm install my-postgres groundhog2k/postgres -f postgres-values.yaml
update the database, user and password

helm list
kubectl get pods

```
### allow role for user academy
go to IAM->roles-> eks-admin->trust relationships -> edit trust policies and add
```
        		{
			"Effect": "Allow",
			"Principal": {
				"AWS": "arn:aws:iam::304303674048:user/academy"
			},
			"Action": "sts:AssumeRole"
		}
```
### connect to cluster
install kubectl if you want<br>
cat /home/ec2-user/.kube/config <br>
create the same file on your machine <br>
install aws cli -> https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html<br?
add key and secret key via aws configure<br> 
aws eks update-kubeconfig --name eks-students --region eu-north-1  --role-arn arn:aws:iam::304303674048:role/eks-admin <br>
install and run lens https://k8slens.dev/<br>

### delete helms
```
helm delete springboot
helm delete my-postgres
https://phoenixnap.com/kb/helm-commands-cheat-sheet
```
### EKS ECR 
create ecr and call it students_eks

### EKS auto deploy
in gitlab go to eks branch -> springboot-> values.yaml put the ecr adress<br>
change registry url and app name to: 416790849346.dkr.ecr.eu-north-1.amazonaws.com and students_staging_eks<br>
adjust the line of assume role: arn:aws:iam::416790849346:role/eks-admin <br>

### add kubeconfig to gitlab
add "UNPROTECTED" "FILE" variable called KUBECONFIG <br>
with value of cat /home/ec2-user/.kube/config <br>

create gitlab-ci.yaml
```
variables:
  DOCKER_REGISTRY: 304303674048.dkr.ecr.eu-north-1.amazonaws.com
  AWS_DEFAULT_REGION: eu-north-1
  APP_NAME: students_eks
  DOCKER_HOST: tcp://docker:2375
  EKS_ROLE: arn:aws:iam::304303674048:role/eks-admin

publish:
  image: 
    name: maven:3.8.1-openjdk-11
    entrypoint: [""]
  services:
    - docker:dind
  before_script:
    - apt-get update
    - apt-get install -y python3-pip
    - pip3 install awscli
    - aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
    - aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
    - aws configure set region $AWS_DEFAULT_REGION
    - export DB_URL=$(aws ssm get-parameter --name "students_staging_eks" --query "Parameter.Value" --output text)
    - echo $DB_URL
    - export DB_PASSWORD=$(aws ssm get-parameter --name "students_staging_eks_password" --query "Parameter.Value" --output text)
    - echo $DB_PASSWORD
    - export DB_USER=$(aws ssm get-parameter --name "students_staging_eks_user" --query "Parameter.Value" --output text)
    - echo $DB_USER
    - sed -i "s#spring.datasource.url=.*#spring.datasource.url=${DB_URL}#g"  src/main/resources/application.properties
    - sed -i "s#spring.datasource.username=.*#spring.datasource.username=${DB_USER}#g"  src/main/resources/application.properties
    - sed -i "s#spring.datasource.password=.*#spring.datasource.password=${DB_PASSWORD}#g"  src/main/resources/application.properties
    - cat src/main/resources/application.properties
    - apt-get update
    - apt-get install -y curl
    - curl -fsSL https://get.docker.com | sh
    - aws --version
    - docker --version
    - mvn --version

  script:
    - mvn clean install
    - aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $DOCKER_REGISTRY
    - docker build -t $DOCKER_REGISTRY/$APP_NAME:$CI_PIPELINE_IID . 
    - docker push $DOCKER_REGISTRY/$APP_NAME:$CI_PIPELINE_IID  

deploy_to_eks:
  stage: deploy
  image: registry.gitlab.com/gitlab-org/cloud-deploy/aws-base:latest
  before_script:
    - export KUBECTL_VERSION=v1.25
    - curl -o kubectl https://s3.us-west-2.amazonaws.com/amazon-eks/1.25.2/2021-07-05/bin/linux/amd64/kubectl
    - chmod +x ./kubectl
    - mkdir -p $HOME/bin && cp ./kubectl $HOME/bin/kubectl && export PATH=$PATH:$HOME/bin
    - curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 > get_helm.sh
    - chmod 700 get_helm.sh
    - ./get_helm.sh
    - aws --version
    - aws sts assume-role --role-arn "$EKS_ROLE" --role-session-name AWSCLI-Session
    - aws eks update-kubeconfig --name eks-students --region $AWS_DEFAULT_REGION --role-arn $EKS_ROLE
    - aws sts get-caller-identity
    
 
  script:
    - sed -i "s/latest/$CI_PIPELINE_IID/" springboot/values.yaml
    - helm upgrade -i springboot springboot/ --values springboot/values.yaml

```

### FRONT AUTOMATION
create a public s3 bucket: eks-stage.nivitzhaky.com (enable static web hosting) <br>

in permissions:
```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicRead",
            "Effect": "Allow",
            "Principal": "*",
            "Action": [
                "s3:GetObject",
                "s3:GetObjectVersion"
            ],
            "Resource": "arn:aws:s3:::eks-stage.nivitzhaky.com/*"
        }
    ]
}
```

### GITLAB front:
create branch eks <br>
env variable 
```
BACKEND_URL_EKS -> https:\/\/eks-stage.nivitzhaky.com\/api
```

.gitlab-ci.yml
```
build stage:  
   image: doctrine/nodejs-aws-cli:v10.19
   stage: build  
   only:    
      - eks  
   script:
      - echo $BACKEND_URL_EKS
      - sed -i "s/backend_url/'$BACKEND_URL_EKS'/" src/environments/environment.ts
      - sed -i "s/backend_url/'$BACKEND_URL_EKS'/" src/environments/environment.prod.ts    
      - cat src/environments/environment.ts
      - cat src/environments/environment.prod.ts
      - npm install --save --legacy-peer-deps    
      # Build App    
      - npm run build 
   artifacts:    
      paths:      
         # Build folder      
         - dist/    
      expire_in: 1 hour

deploy stage:  
   image: python:latest  
   stage: deploy  
   only:    
      - ecs  
   script:    
      - pip install awscli    
      - aws s3 sync ./dist/webapp/ s3://eks-stage.nivitzhaky.com   
```
### Cloudfront
create distribution<br>
origin1-> select s3 bucket -> use website endpoint -> caching disabled <br>
alternate domain: eks-stage.nivitzhaky.com
cname (give name) eks-stage.nivitzhaky.com and copy cloudfront distribution url<br>
hosted zones-> domain -> create record ->
<br><br>
origin2->
http only ->  ekslb -> 8081->  choose all allowed http methods<br>
alternate domain name-> eks-stage.nivitzhaky.com
<br>
behaviours: <br>
api/* -> allowed methods all -> caching all <br>
* -> leave default

cname (give name) eks-stage.nivitzhaky.com and copy cloudfront distribution url

## CLEANUP

delete ecs-stage-cluster<br>
eksctl delete  cluster -f eks.yaml  <br>
terminate the ec2 instance<br>
delete load balancers<br>
delete RDS<br>

