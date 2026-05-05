# Contributing to InfraRecord

## Development Workflow

1. Fork the repository
2. Create a feature branch: `git checkout -b feat/description`
3. Make changes with clear, atomic commits
4. Run tests locally before pushing
5. Open a Pull Request with detailed description

## Commit Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `chore:` Maintenance tasks
- `test:` Test additions/modifications
- `refactor:` Code restructuring

## Testing

### Backend
```bash
cd backend
mvn clean test
```

### AI Engine
```bash
cd ai-engine
pytest tests/ -v
```

### Frontend
```bash
cd frontend
npm run lint
npm run build
```

## Code Review

All submissions require review before merging. Ensure:
- Tests pass
- No linting errors
- Documentation updated if needed
- Commit messages follow convention
