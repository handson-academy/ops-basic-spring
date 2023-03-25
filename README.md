# devops
Niv Itzhaky<br>
https://www.linkedin.com/in/nivitzhaky/<br>
niv.itzhaky@gmail.com<br>
0525236451<br>

link to presentation -> https://docs.google.com/presentation/d/1fb-fyxiP5T-3Xdn1d7XT1kwAY2jOSOCy1TY5jITSOtw/edit?usp=sharing <br>
create your own account <br>
fork https://github.com/handson-academy/ops-basic-spring/ to your own git <br>

## ec2
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

create ec2 branch <br>
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
git clone git@github.com:nivitzhaky/ops-basic-spring.git -b ec2
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

### DOCKERIZE
```
sudo docker build . -t backend
sudo docker login
nivitzhaky
Jul201789#
sudo docker tag backend nivitzhaky/backend
sudo docker push nivitzhaky/backend

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
### DOCKER AUTOMATION
add the following secrets:
```
DOCKERHUB_USERNAME = nivitzhaky
DOCKERHUB_TOKEN = dckr_pat_wNsuA4lJiuBnc4iCsNCmxjCVjc4
EC2_INSTANCE_PUBLIC_IP = 13.50.235.108

```


add .github/workflows/build.yml
```
name: Build and Deploy

on:
  push:
    branches:
      - ec2

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
          password: ${{ secrets.SSH_PASSWORD }} # store the SSH password as a secret in the repository
          script: |
            cd /home/ec2-user/ops-basic-spring
	    sed -i 's/image: nivitzhaky\/backend:.*/image: nivitzhaky\/backend:v1.0.${{ github.run_number }}/g' /home/ec2-user/ops-basic-spring/docker-compose.yml
            sudo /usr/local/bin/docker-compose  down || true
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
          aws s3 sync ./dist s3://${{ env.S3_BUCKET_NAME }} --delete
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


### Cloudfront
create distribution<br>
origin1->
http only -> 8080-> origin = select load balancer => choose all allowed http methods<br>
alternate domain name-> ec2-stage.nivitzhaky.com
Custom SSL certificate - optional -> request new certificate, domainname = nivitzhaky.com, *.nivitzhaky.com<br>
validate be dns-> make aws create cname
origin2-> select s3 bucket ->
Default root object: index.html->legacy access identifiers-> create new OAI->

hosted zones-> domain -> create record ->

cname (give name) ec2-stage.nivitzhaky.com and copy cloudfront distribution url


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


##AIM
go to AIM and create a user with programatic access and console sign in (admin credentials)
```
accountid = 
access_key=
secret_key=
username=academy
password=Unix11!@
```



on gitlab, backend service. <br>
goto: settings -> ci/cd <br>
expand variables, update: (unprotect the vars)
```
AWS_ACCESS_KEY_ID=
AWS_DEFAULT_REGION=us-east-1
AWS_SECRET_ACCESS_KEY=
CI_AWS_ECS_CLUSTER=ecs-stage-cluster
CI_AWS_ECS_SERVICE=ecs-stage-service
```


###Parameter store
```
#backend_url_ecs='https:\/\/ecs-stage.nivitzhaky.com\/api'
#backend_url_eks=https:\/\/eks-stage.nivitzhaky.com\/api
students_staging_ecs=jdbc:mysql:\/\/database-2.cmyyngkp9f7o.us-east-1.rds.amazonaws.com:3306\/students_stage_ecs
students_staging_ecs_user=students_staging_ecs
students_staging_ecs_password=students_staging_ecs
students_staging_eks=jdbc:mysql:\/\/database-2.cmyyngkp9f7o.us-east-1.rds.amazonaws.com:3306\/students_stage_eks
students_staging_eks_user=students_staging_eks
students_staging_eks_password=students_staging_eks
```
###ECR
create an ECR, call it students


##ECS
update ECR in the gitlab.ci<br>
create taskdefinition ->  ecs-task-definition <br>
containername ->student-ecs-container<br>
imageuri-> copy from ecr table<br>
container port-> 8080 <br>
cpu - 1, memory- 4 <br>
configure healthcheck:  CMD-SHELL, curl -f http://localhost:8080/actuator/health <br>



create a service: <br>
faragate-> service-> family=ecs-task-definition->servicename = ecs-stage-service <br>


create applicaiton load balancer (ec2 load balancer)


springboot-lb
define vpc (default)
defive availability zones (at least 3)
port 8080 <br>
create a target group-> type=ipaddress -> students-ecs-tg-> port 8080->configure healthcheck:  /actuator/health-> next-> copy private ip from ecs ports=8080-> include as pending <br>


(check if needed) after creating service and task go to security group of the service and allow inbound 8080<br>
when the task goes up we can test by ip:8080/swagger-ui.html

##front repository:
create a public s3 bucket: ecs-stage.nivitzhaky.com   (enable static web hosting)
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
            "Resource": "arn:aws:s3:::ecs-stage.nivitzhaky.com/*"
        }
    ]
}
```
### Cloudfront
create distribution<br>
origin1->
http only -> 8080-> origin = select load balancer => choose all allowed http methods<br>
alternate domain name-> ecs-stage.nivitzhaky.com
Custom SSL certificate - optional -> request new certificate, domanname = *.nivitzhaky.com<br>
validate be dns-> make aws create cname
origin2-> select s3 bucket ->
Default root object: index.html->legacy access identifiers-> create new OAI->

hosted zones-> domain -> create record ->

cname (give name) ecs-stage.nivitzhaky.com and copy cloudfront distribution url



### EKS
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
install jp
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
turn off credential managment in settings icone -> aws settings -> turn offf temporary credentials
```
(maybe not needed)aws cloud9 update-environment  --environment-id $C9_PID --managed-credentials-action DISABLE
rm -vf ${HOME}/.aws/credentials

(not needed done with ui)aws sts assume-role --role-arn "arn:aws:iam::416790849346:role/eks-admin" --role-session-name A
go to top right -> manage ec2 instance -> actions -> security -> modify aim role -> select eks-admin

aws kms create-alias --alias-name alias/eks --target-key-id $(aws kms create-key --query KeyMetadata.Arn --output text)
export MASTER_ARN=$(aws kms describe-key --key-id alias/eks --query KeyMetadata.Arn --output text)
echo "export MASTER_ARN=${MASTER_ARN}" | tee -a ~/.bash_profile

# install eks-ctl
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
  instanceType: t3.small
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

## EKS deploy
in gitlab go to eks2 branch -> springboot-> values.yaml put the ecr adress<br>
in gitlab-ci.yml<br>
change registry url and app name to: 416790849346.dkr.ecr.us-east-1.amazonaws.com and students_staging_eks<br>
adjust the line of assume role: arn:aws:iam::416790849346:role/eks-admin <br>
go to IAM->roles-> eks-admin->edit and add
```
		{
			"Effect": "Allow",
			"Principal": {
				"AWS": "arn:aws:iam::308312479356:user/academy"
			},
			"Action": "sts:AssumeRole"
		}
```
add "UNPROTECTED" "FILE" variable called KUBECONFIG
with value of cat /home/ec2-user/.kube/config



create a public s3 bucket: eks-stage.nivitzhaky.com
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
        },
        {
            "Sid": "AllowPublicRead",
            "Effect": "Allow",
            "Principal": {
                "AWS": "*"
            },
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::eks-stage.nivitzhaky.com/*"
        }
    ]
}
```




## route configuration

cloudfront eks-> create distribution<br>
2 origins, Default root object: index.html
1. eks-stage
   path: /api/* http only -> 8081-> origin = select load balancer => choose all allowed http methods<br>
2. s3 bucket
   use: legacy access identities, check: no, I will update bucket policy

alternate domain name-> eks-stage.nivitzhaky.com
Custom SSL certificate use domanname = *.nivitzhaky.com<br>
validate be dns-> create cname eks-stage.nivitzhaky.com

