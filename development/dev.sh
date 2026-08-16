#!/bin/bash
# Joko Security Development - Ultra Simple
set -e

case "${1:-help}" in

    "build")
        echo "🔨 Building..."
        cd .. && ./mvn.sh clean compile -q -Dmaven.javadoc.skip=true
        ../mvn.sh clean compile -q
        echo "✅ Done!"
        ;;
        
    "dev")
        echo "🚀 Starting H2..."
        ../mvn.sh spring-boot:run -q
        ;;
        
    "dev-pg")
        echo "🚀 Starting PostgreSQL..."
        ../mvn.sh spring-boot:run -q -Dspring-boot.run.profiles=postgres
        ;;
        
    "db-up")
        echo "🐘 Starting PostgreSQL..."
        docker-compose up -d db
        echo "✅ PostgreSQL: localhost:5433, Adminer: localhost:8081"
        ;;
        
    "db-down")
        docker-compose down
        echo "✅ Stopped"
        ;;
        
    "db-reset")
        echo "🔄 Resetting..."
        docker-compose down -v >/dev/null 2>&1
        docker-compose up -d db >/dev/null 2>&1
        echo "✅ Fresh database ready"
        ;;
        
    "test")
        cd .. && ./test.sh
        ;;
        
    "clean")
        cd .. && ./mvn.sh clean -q && cd development && ../mvn.sh clean -q
        echo "✅ Cleaned"
        ;;
        
    "install")
        cd .. && ./mvn.sh clean install -q -Dmaven.javadoc.skip=true
        echo "✅ Installed"
        ;;
        
    *)
        echo "Joko Security Dev Commands:"
        echo "  build       dev         dev-pg"
        echo "  db-up       db-down     db-reset"
        echo "  test        clean       install"
        ;;
esac