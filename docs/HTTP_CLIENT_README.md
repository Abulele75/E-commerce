# IntelliJ HTTP Client

Select the `local` environment in IntelliJ before running requests. Copy `../Backend/http/http-client.private.env.example.json` to `../Backend/http/http-client.private.env.json` and replace its placeholders with local credentials; the private file is ignored and must not be committed.

Run `../Backend/http/auth.http` first: register the sample customer only when needed, then run the customer and administrator logins to capture `customerAuthToken` and `adminAuthToken`. Use `../Backend/http/cart.http` for the current customer's cart and `../Backend/http/catalog/catalog.http` for representative catalog calls.

Seed products by running the files under `catalog/seed/` with an administrator token. Each SKU is unique and the API rejects duplicate SKUs, so do not rerun a category against an already-seeded database unless those products were removed first.

There are seed files for smart watches, smartphones, laptops, accessories, and computer components. The application supports desktop computers, but no desktop seed payload exists and none has been invented here.

Password recovery examples are in `../Backend/http/password-reset.http`. In local development, the forgot-password response exposes a single-use reset token that expires after 15 minutes. Set `PASSWORD_RESET_EXPOSE_TOKEN=false` outside local development.
