# 🧮 WataERP API

Spring Boot で構築した顧客管理APIです。  
ドメイン駆動設計（DDD）を意識しつつ、バリデーション、例外ハンドリング、レスポンス標準化など  
現場レベルの設計品質を意識して構築しました。

---

![Build](https://github.com/watashun0715/wata-erp/actions/workflows/ci.yml/badge.svg)
![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

## 🚀 プロジェクト概要

| 項目 | 内容 |
|------|------|
| フレームワーク | Spring Boot 3.x |
| ビルドツール | Gradle |
| DB | PostgreSQL（Flywayによるマイグレーション管理） |
| API仕様管理 | OpenAPI (Swagger UI) |
| 実行環境 | Docker Compose |
| Javaバージョン | 17以上 |

### 🎯 目的
- ERP領域の基礎理解と業務システム構築スキルの習得  
- バリデーション、例外処理、REST API設計の実践  
- ポートフォリオとしての設計・品質の可視化  

---

## 🧩 主な機能

### 顧客管理（/api/v1/customers）
| メソッド | エンドポイント | 概要 |
|----------|----------------|------|
| `POST` | `/api/v1/customers` | 顧客登録（重複コード検知） |
| `GET` | `/api/v1/customers/all` | 全顧客取得 |
| `GET` | `/api/v1/customers/{code}` | 顧客コードで単一取得 |
| `GET` | `/api/v1/customers/by-codes?codes=C001,C002` | 顧客コード複数指定で取得 |

---

## 🧱 技術構成

### 主なライブラリ
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-jpa`
- `org.flywaydb:flyway-core`
- `org.postgresql:postgresql`
- `springdoc-openapi-starter-webmvc-ui`

---

## ⚙️ エラーハンドリング

統一フォーマットで例外レスポンスを返却。  
全APIで一貫したエラー構造を実現しました。

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "入力値が不正です。",
  "path": "/api/v1/customers",
  "timestamp": "2025-10-07T08:57:52.258Z",
  "violations": [
    {
      "field": "creditLimit",
      "reason": "値の型が不正です。期待: BigDecimal",
      "rejectedValue": "aaa"
    }
  ]
}

```

---

## 🧠 設計上の工夫ポイント

### 共通例外ハンドラ
- @RestControllerAdvice による全API共通のエラー管理。
- BeanValidation / JSON変換 / DB制約 / 404 / 500 を個別に処理。

### Mapperの導入
- Entity ⇔ DTO を分離し、ドメインとI/Oの責務を明確化。

### サービスクラスの統合設計
- 顧客の全取得・単一取得・複数取得を同一メソッドで処理する構成。
- → コード重複を排除し、保守性向上。

### FlywayによるDBマイグレーション管理
- DB構成をソース管理下に置き、再現性の高い開発環境を実現。

