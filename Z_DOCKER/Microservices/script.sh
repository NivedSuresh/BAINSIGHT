#!/bin/bash

# Connect to PostgreSQL as the 'postgres' user
psql -U postgres <<EOF
-- Create the 'order_persistence' database if it does not exist
CREATE DATABASE IF NOT EXISTS order_persistence;

-- Create the 'portfolio_service' database if it does not exist
CREATE DATABASE IF NOT EXISTS portfolio_service;
EOF
