# ======================================
# Terraform 기본 설정
# ======================================
terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
}

provider "aws" {
  region = var.region
}

# ======================================
# 1️⃣ VPC / Subnet / 인터넷 연결
# ======================================
resource "aws_vpc" "vpc_1" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = { Name = "${var.prefix}-vpc-1" }
}

resource "aws_subnet" "subnet_1" {
  vpc_id                  = aws_vpc.vpc_1.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "${var.region}a"
  map_public_ip_on_launch = true

  tags = { Name = "${var.prefix}-subnet-1" }
}

resource "aws_internet_gateway" "igw_1" {
  vpc_id = aws_vpc.vpc_1.id
  tags   = { Name = "${var.prefix}-igw-1" }
}

resource "aws_route_table" "rt_1" {
  vpc_id = aws_vpc.vpc_1.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw_1.id
  }

  tags = { Name = "${var.prefix}-rt-1" }
}

resource "aws_route_table_association" "association_1" {
  subnet_id      = aws_subnet.subnet_1.id
  route_table_id = aws_route_table.rt_1.id
}

# ======================================
# 2️⃣ Security Group
# ======================================
resource "aws_security_group" "sg_1" {
  name   = "${var.prefix}-sg-1"
  vpc_id = aws_vpc.vpc_1.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.prefix}-sg-1" }
}

# ======================================
# 3️⃣ IAM Role + Instance Profile
# ======================================
resource "aws_iam_role" "ec2_role_1" {
  name = "${var.prefix}-ec2-role-1"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": { "Service": "ec2.amazonaws.com" },
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "s3_full_access" {
  role       = aws_iam_role.ec2_role_1.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
}

resource "aws_iam_role_policy_attachment" "ec2_ssm" {
  role       = aws_iam_role.ec2_role_1.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2RoleforSSM"
}

resource "aws_iam_instance_profile" "instance_profile_1" {
  name = "${var.prefix}-instance-profile-1"
  role = aws_iam_role.ec2_role_1.name
}

locals {
  ec2_user_data_base = <<-END_OF_FILE
#!/bin/bash
yum install docker -y
systemctl enable docker
systemctl start docker

yum install git -y

# Swap 파일 생성
sudo dd if=/dev/zero of=/swapfile bs=128M count=32
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo sh -c 'echo "/swapfile swap swap defaults 0 0" >> /etc/fstab'

# Docker network
docker network create common || true
END_OF_FILE
}

# ======================================
# 4️⃣ EC2 인스턴스 (1개만)
# ======================================
resource "aws_instance" "ec2_1" {
  ami                         = "ami-062cddb9d94dcf95d"
  instance_type               = "t3.micro"
  subnet_id                   = aws_subnet.subnet_1.id
  vpc_security_group_ids      = [aws_security_group.sg_1.id]
  associate_public_ip_address = true
  iam_instance_profile        = aws_iam_instance_profile.instance_profile_1.name

  root_block_device {
    volume_type = "gp3"
    volume_size = 12
  }

  user_data = <<-EOF
${local.ec2_user_data_base}
EOF

  tags = { Name = "${var.prefix}-ec2-1" }
}
