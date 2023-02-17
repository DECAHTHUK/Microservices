<html>
<head>
    <meta charset="utf-8">
    <title>Wallet</title>
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
<h1>Wallet</h1>
<table>
    <tr>
        <th>Name</th>
        <th>Quantity</th>
    </tr>
    <#list currencies as currency>
        <tr>
            <td>${currency.charCode}</td>
            <td>${currency.quantity}</td>
        </tr>
    </#list>
</table>
</body>