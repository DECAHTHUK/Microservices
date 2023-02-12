<html>
<head>
    <meta charset="utf-8">
    <title>Transactions</title>
    <style>
        table {
            color: black;
            background-color: #ffa50d;
            border: 1px solid black;
            border-collapse: collapse;
        }
        body {
            background-color: #ffc360;
        }
    </style>
</head>
<body>
<h3>Return to the <a href="/banking/menu">menu</a></h3>
<h1>Transactions(pagination is for weak people, eat this!)</h1>
<table>
    <tr>
        <th>N</th>
        <th>Message</th>
        <th>Code</th>
        <th>Quantity</th>
        <th>Time</th>
    </tr>
    <#list transactions as transaction>
        <tr>
            <td>${transaction?index + 1}</td>
            <td>${transaction.message}</td>
            <td>${transaction.charCode}</td>
            <td>${transaction.quantity}</td>
            <td>${transaction.time}</td>
        </tr>
    </#list>
</table>
</body>