CREATE TABLE metoo_ip_statistics_result (
                                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                                            accessCount INTEGER,
                                            ip TEXT,
                                            name TEXT,
                                            "rank" INTEGER,
                                            statisticsTime TEXT,
                                            "type" TEXT
);
ALTER TABLE metoo_sureying_log ADD parentId INTEGER;
