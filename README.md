# 🧮 WataERP API

Spring Boot で構築した顧客管理APIです。  
ドメイン駆動設計（DDD）を意識しつつ、バリデーション、例外ハンドリング、レスポンス標準化など  
現場レベルの設計品質を意識して構築しました。

---

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

### レイヤー構成
