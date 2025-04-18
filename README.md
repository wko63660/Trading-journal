# Trading Journal – Backend (Spring Boot)

This is the backend service for the Trading Journal web app — a personal trading analysis tool that helps you import, track, and analyze your trades. The backend is built with Java and Spring Boot, using PostgreSQL for data persistence and JWT for authentication.


## Features Implemented

1. JWT-based authentication

2. Role-based access control (USER, ADMIN)

3. Trade entity supporting options, stocks, and futures

4. Parsing and importing .TLG trade logs

5. Matching open and close legs using a custom FIFO/avg-cost matcher

6. Calculating PnL, entry/exit, and holding time

7. User-linked trades with relational mapping

8. Dashboard-ready aggregate statistics (Win Rate, Profit Factor, etc.)
