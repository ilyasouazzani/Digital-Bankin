# Digital Banking Application

Application bancaire fullstack développée avec **Spring Boot 3**, **Angular 17**, **Spring Security JWT** et un **Chatbot IA RAG** intégré via Spring AI et Telegram.

---

## Architecture du projet

```
Digital-Bankin/
├── digital-banking-backend/        # API REST Spring Boot
│   └── src/main/java/com/digitalbanking/
│       ├── entities/               # JPA : Customer, BankAccount, CurrentAccount, SavingAccount, AccountOperation
│       ├── repositories/           # Spring Data JPA avec pagination
│       ├── dtos/                   # DTOs (jamais d'entités exposées directement)
│       ├── mappers/                # BankAccountMapperImpl (BeanUtils)
│       ├── exceptions/             # Exceptions métier + GlobalExceptionHandler
│       ├── services/               # BankAccountService (CRUD, débit, crédit, virement)
│       ├── web/                    # REST Controllers (Swagger OpenAPI)
│       ├── security/               # JWT : AppUser, AppRole, filtres, UserDetailsService
│       └── chatbot/                # RAG Chatbot : Spring AI, Telegram, Function Calling
│
└── digital-banking-frontend/       # SPA Angular 17
    └── src/app/
        ├── models/                 # Interfaces TypeScript
        ├── services/               # AuthService, CustomerService, BankAccountService
        ├── guards/                 # AuthGuard (routes protégées)
        ├── interceptors/           # JwtInterceptor (injection token Bearer)
        └── components/
            ├── login/              # Formulaire de connexion JWT
            ├── navbar/             # Navigation responsive Bootstrap 5
            ├── dashboard/          # KPI + 3 graphiques Chart.js
            ├── customers/          # CRUD clients avec recherche
            ├── accounts/           # Comptes + historique paginé
            ├── operations/         # Débit / Crédit / Virement
            └── chatbot/            # Interface IA (RAG)
```

---

## Démarrage rapide

### Backend (Spring Boot)

```bash
cd digital-banking-backend

# Variables d'environnement obligatoires pour l'IA et Telegram
export OPENAI_API_KEY=sk-...
export TELEGRAM_BOT_TOKEN=...
export TELEGRAM_BOT_USERNAME=monbot

mvn spring-boot:run
```

| URL | Description |
|-----|-------------|
| `http://localhost:8085/swagger-ui.html` | Documentation API interactive |
| `http://localhost:8085/h2-console` | Console base de données H2 |
| `POST /api/auth/login` | Authentification JWT |

**Comptes de démo :**
- `admin / admin1234` (ROLE_ADMIN + ROLE_USER)
- `user1 / user1234` (ROLE_USER)

### Frontend (Angular)

```bash
cd digital-banking-frontend
npm install
npm start
# → http://localhost:4200
```

---

## Fonctionnalités

### Partie 1 — API REST
- Gestion CRUD clients et comptes bancaires
- Héritage JPA `SINGLE_TABLE` (CurrentAccount, SavingAccount)
- Opérations : débit, crédit, virement (atomique)
- Historique paginé des opérations
- Documentation Swagger / OpenAPI

### Partie 2 — Frontend Angular
- Dashboard analytique avec Chart.js (doughnut, pie, bar)
- CRUD clients avec recherche en temps réel
- Liste des comptes + historique paginé
- Formulaire d'opérations (débit/crédit/virement)
- Interface chatbot IA intégrée

### Partie 3 — Sécurité JWT
- Authentification Stateless Spring Security
- Entités AppUser / AppRole avec BCrypt
- Filtres JWT : génération à la connexion, validation par requête
- Audit : chaque opération trace l'utilisateur connecté
- Endpoints profil et changement de mot de passe

### Partie 4 & 5 — Chatbot IA RAG + Telegram
- Architecture RAG : knowledge base documentaire + function calling temps réel
- Détection d'intention : solde, statistiques, info client
- Historique multi-tours persisté en base
- Bot Telegram : `/start`, `/help`, `/stats`, messages libres
- Interface web intégrée dans Angular

---

## Variables d'environnement

| Variable | Description | Obligatoire |
|----------|-------------|-------------|
| `OPENAI_API_KEY` | Clé API OpenAI | Oui (chatbot) |
| `TELEGRAM_BOT_TOKEN` | Token BotFather | Non (optionnel) |
| `TELEGRAM_BOT_USERNAME` | Username du bot | Non (optionnel) |

---

## Technologies

**Backend :** Spring Boot 3.2, Spring Data JPA, Spring Security, JJWT 0.12, Spring AI, Telegram Bots, H2, Lombok, MapStruct, Swagger OpenAPI 2.1

**Frontend :** Angular 17 (Standalone), TypeScript, Bootstrap 5, Chart.js, ng2-charts, RxJS

