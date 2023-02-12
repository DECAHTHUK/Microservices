<html>
<head>
    <meta charset="utf-8">
    <title>Currencies</title>
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
<h1>Currencies</h1>
<table>
    <tr>
        <th>N</th>
        <th>Num code</th>
        <th>Char code</th>
        <th>Nominal</th>
        <th>Name</th>
        <th>Course(to RUB)</th>
    </tr>
    <#list currencies as currency>
        <tr>
            <td>${currency?index + 1}</td>
            <td>${currency.numCode}</td>
            <td>${currency.charCode}</td>
            <td>${currency.nominal}</td>
            <td>${currency.currencyName}</td>
            <td>${currency.power}</td>
        </tr>
    </#list>
</table>
</body>