const mysql = require('mysql');
const lineReader = require('line-reader');

const connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'ChatApp2019!',
    database: 'codenames'
});

connection.connect(function(err) {
    if (err) throw err;
    console.log("Connected to db")
    lineReader.eachLine('szavak.txt', 'utf8', function(line) {
        let sql = `INSERT INTO word (word) VALUES ('${line}')`;
        connection.query(sql, null, function (er, result) {
            if (er) throw er;
            console.log("Record inserted");
        })
    })
})