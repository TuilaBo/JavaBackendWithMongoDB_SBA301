# GitHub Actions Zero to Hero

## What GitHub Actions Is

GitHub Actions is CI/CD that runs from files inside `.github/workflows/`.

For this project, the first goal is simple:

1. Push code
2. Let GitHub compile the project automatically
3. Catch broken code before merge

## The First Workflow In This Repo

File: `.github/workflows/ci.yml`

Current behavior:

1. Runs on `push` to `main`, `develop`, and `feature/**`
2. Runs on `pull_request` to `main` and `develop`
3. Can also be run manually with `workflow_dispatch`
4. Checks out the repository
5. Sets up Java 17
6. Restores Maven cache
7. Runs `mvn -B -DskipTests compile`

This is intentionally small and stable. It proves the code can compile in a clean machine.

## Read The Workflow Step By Step

### `name`

Human-readable workflow name shown in GitHub Actions UI.

### `on`

Defines when the workflow runs.

- `push`: run when code is pushed
- `pull_request`: run when a PR is opened or updated
- `workflow_dispatch`: run manually from the GitHub UI

### `jobs`

A workflow can have one or many jobs.

This repo starts with one job: `compile`.

### `runs-on`

The temporary machine used by GitHub to run the job.

### `steps`

Commands or reusable actions executed in order.

- `actions/checkout@v4`: download your repository into the runner
- `actions/setup-java@v4`: install Java and cache Maven dependencies
- `mvn -B -DskipTests compile`: compile the project without interactive prompts

## Learning Path

### Level 1: Understand Basic CI

Goal:

- Know when workflows run
- Read logs
- Know why a build passed or failed

Practice:

1. Push a tiny code change
2. Open the Actions tab on GitHub
3. Read each step log

### Level 2: Add Real Test Execution

When your tests stop depending on local-only config, change:

`mvn -B -DskipTests compile`

to:

`mvn -B test`

Goal:

- Make CI verify behavior, not just syntax

### Level 3: Split Build And Test

Later you can create separate jobs:

1. `compile`
2. `test`
3. `package`

Goal:

- Clearer failures
- Better pipeline structure

### Level 4: Add Branch Rules

Use GitHub branch protection so `main` cannot be merged unless CI passes.

Goal:

- Prevent broken code from reaching main

### Level 5: Add CD

After CI is stable, deploy only from `main`.

Typical rule:

1. Push feature branch -> CI only
2. Merge into `main` -> deploy

## Good Practices

1. Keep the first workflow small.
2. Make CI deterministic.
3. Do not deploy from every branch.
4. Do not put secrets directly in workflow YAML.
5. Prefer env vars and GitHub Secrets for sensitive values.

## What To Do Next In This Repo

1. Push this workflow to GitHub
2. Confirm the `compile` job passes
3. Fix the test environment so `mvn test` can run in CI
4. Add a second workflow for deployment later
