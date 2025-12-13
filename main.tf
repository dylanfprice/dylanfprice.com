terraform {
  backend "s3" {
    bucket  = "dylanfprice-dot-com-terraform"
    key     = "terraform.tfstate"
    region  = "us-west-2"
    profile = "dfp"
  }
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.14"
    }
  }
  required_version = ">= 1.13.0"
}

provider "aws" {
  alias = "application"
  region  = "us-west-2"
  profile = "dfp"
}

resource "aws_servicecatalogappregistry_application" "dylanfprice_dot_com" {
  provider    = aws.application
  name        = "dylanfprice-dot-com"
  description = "dylanfprice.com"
}

provider "aws" {
  region  = "us-west-2"
  profile = "dfp"
  default_tags {
    tags = aws_servicecatalogappregistry_application.dylanfprice_dot_com.application_tag
  }
}

locals {
  domain_name = "dylanfprice.com"
}

resource "aws_route53domains_registered_domain" "dylanfprice_com" {
  domain_name = local.domain_name
}

resource "aws_route53_zone" "main" {
  name = local.domain_name
}

resource "aws_route53_record" "a" {
  zone_id = aws_route53_zone.main.zone_id
  name    = ""
  type    = "A"
  ttl     = 1800
  records = ["46.23.81.157"]
}

resource "aws_route53_record" "aaaa" {
  zone_id = aws_route53_zone.main.zone_id
  name    = ""
  type    = "AAAA"
  ttl     = 1800
  records = ["2a03:6000:1813:1337::157"]
}

resource "aws_route53_record" "mx" {
  zone_id = aws_route53_zone.main.zone_id
  name    = ""
  type    = "MX"
  ttl     = 1800
  records = ["10 mailserver.purelymail.com."]
}

resource "aws_route53_record" "spf" {
  zone_id = aws_route53_zone.main.zone_id
  name    = ""
  type    = "TXT"
  ttl     = 1800
  records = [
    "purelymail_ownership_proof=2f1e2e009bf8dab0e44d77aba59950248f059da7e72f6dd73749caecbe22173eb0498a9ae02d74eff385c451d2b102805aea139ec965c7d1b624299b7b48e2c1",
    "v=spf1 include:_spf.purelymail.com ~all"
  ]
}

resource "aws_route53_record" "dkim_1" {
  zone_id = aws_route53_zone.main.zone_id
  name = "purelymail1._domainkey"
  type = "CNAME"
  ttl = 1800
  records = ["key1.dkimroot.purelymail.com."]
}

resource "aws_route53_record" "dkim_2" {
  zone_id = aws_route53_zone.main.zone_id
  name = "purelymail2._domainkey"
  type = "CNAME"
  ttl = 1800
  records = ["key2.dkimroot.purelymail.com."]
}

resource "aws_route53_record" "dkim_3" {
  zone_id = aws_route53_zone.main.zone_id
  name = "purelymail3._domainkey"
  type = "CNAME"
  ttl = 1800
  records = ["key3.dkimroot.purelymail.com."]
}

resource "aws_route53_record" "dmarc" {
  zone_id = aws_route53_zone.main.zone_id
  name = "_dmarc"
  type = "CNAME"
  ttl = 1800
  records = ["dmarcroot.purelymail.com."]
}
