<html>
<head>
    <meta charset="utf-8">
    <title>Transfer</title>
    <style>
        @keyframes badassThang {
            0% {
                animation-timing-function: ease-in;
                opacity: 0;
                transform: translateY(-250px);
            }

            38% {
                animation-timing-function: ease-out;
                opacity: 1;
                transform: translateY(0);
            }

            55% {
                animation-timing-function: ease-in;
                transform: translateY(-65px);
            }

            72% {
                animation-timing-function: ease-out;
                transform: translateY(0);
            }

            81% {
                animation-timing-function: ease-in;
                transform: translateY(-28px);
            }

            90% {
                animation-timing-function: ease-out;
                transform: translateY(0);
            }

            95% {
                animation-timing-function: ease-in;
                transform: translateY(-8px);
            }

            100% {
                animation-timing-function: ease-out;
                transform: translateY(0);
            }
        }
        body {
            background-color: #ffc360;
        }
        img {
            animation-name: badassThang;
            animation-duration: 2s;
        }
    </style>
</head>
<body>
    <h3>Return to the <a href="/banking/menu">menu</a></h3>
    <h1>Successful transaction.</h1>
    <h2>New balance is ${newBalanceFrom} ${code}.</h2>
    <p>&nbsp;</p>
    <p><img src="https://i.postimg.cc/yxxdf5V3/Puz-Q4a-OQ0-TU.jpg" alt="Типа веселый" width="560" height="560"></p>
</body>
</html>