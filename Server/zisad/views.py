from django.http import HttpResponse
import numpy as np
from scipy.integrate import odeint
from rest_framework.views import APIView
from rest_framework.response import Response
from datetime import datetime
import math    
import json


total_population = 1.614*pow(10, 8)
start_date = '08/03/2020'
initial_infected = 3


total_day = 365
p = 0.01
E0, R0 = 0, 0
model = 0
delta, miu = 1/5, 1/10
bd_incubation_period = 5
bd_infection_period = 10
beta_start = 0.54

t = np.linspace(17, total_day, total_day)

date_list = [start_date, '18/04/2020', '23/04/2020', '01/05/2020', '06/05/2020', '08/05/2020', '23/05/2020', '02/06/2020']
beta_list = [beta_start, 0.42, 0.2, 0.32, 0.1, 0.225, 0.20, 0.21]


covid_info = "<font size='5'>২০১৯ সালের ডিসেম্বরের শেষের দিকে চাইনার উহান শহরে ছড়িয়ে পড়া COVID-19 (2019-nCoV) ভাইরাসটির বাংলাদেশের সংক্রমণের প্রথম রোগী ২০২০ সালের <b>৮ মার্চ</b> শনাক্ত হয়। সাধারণভাবে একটি রোগ কতটা ছোঁয়াচে, তা নির্ধারণ করা হয় একজন রোগী আরও কতজনকে সংক্রামিত করতে পারেন, সেই সংখ্যার ওপর। এই সংখ্যাকে বলা হয় <b>বেসিক রিপ্রোডাকশন নাম্বার (R<sub>0</sub>)</b>, এবং তা থেকে এও বোঝা যায় যে এই মহামারীকে নিয়ন্ত্রণ করা কতটা কঠিন হতে পারে। বিশ্ব স্বাস্থ্য সংস্থার (WHO) প্রাথমিক অনুমান, 2019-nCoV’র বেসিক রিপ্রোডাকশন নাম্বারের ব্যাপ্তি হলো <b>১.৪ থেকে ২.৫</b>, যার মানে এটি SARS-এর মতোই, এবং ইনফ্লুয়েঞ্জার চেয়ে বেশি, ছোঁয়াচে। তবে বেইজিংয়ের চাইনিজ অ্যাকাডেমি অফ সায়েন্সেস-এর একটি দল বলছে, এই সংখ্যা হওয়া উচিত <b>৪.০৮</b>। কিন্তু শুরু থেকে বাংলাদেশে <b>R<sub>0</sub></b> এর মান ছিল <b>৫.৪</b>। তবে বাংলাদেশের সরকারের নেয়া বিভিন্ন পদক্ষেপ এবং লকডাউনের কারণে গত <b>১৮ই এপ্রিল</b> এই সংখ্যাটি <b>১৮.৫%</b> কমে <b>৪.৪</b> হয়। গত <b>২৩ এপ্রিল</b> থেকে এটি পুনরায় <b>৫৪.৫%</b> কমে <b>২.০</b> হয়েছে। কিন্তু <b>পহেলা মে</b> থেকে এটি <b>তিন পঞ্চমাংশ</b> বৃদ্ধি পেয়ে <b>৩.২</b> হয়। আমাদের এই মডেলটি প্রায় <b>৯৪%-৯৮%</b> সঠিকভাবে মোট সংক্রমণের সংখ্যা গননা করতে পারে।"
developer_info = "<br><br> This App is being developed as a part of the research entitled “Application of Artificial Intelligence in Covid-19” , where Professor Dr. Mohammad Shahadat Hossain (MSH), Department of Computer Science and Engineering of University of Chittagong, Bangladesh is acting as the Principle Investigator. The other team members include Professor Dr. Mohammed Sazzad Hossain (SH), Member, University Grants Commission of Bangladesh, Mr. Sharif Noor Zisad (SNZ), Department of Computer Science and Engineering, University of Chittagong and Professor Dr. Karl Andersson (KL), Luleå University of Technology, Sweden.</font>"

# information = covid_info+developer_info

information = developer_info


def index(request):
    return HttpResponse("Great! the server is working")


class COVID_model(APIView):

    def get(self, request):    
        json_data = parse_data(total_population, initial_infected, date_list, beta_list, bd_incubation_period, bd_infection_period)
        return HttpResponse(json_data)



    def post(self, request):
        global model
        request_body = request.POST.copy()

        R1 = float(request_body['R1'])
        intervention_date = request_body['intervention_date']
        model = int(request_body['model'])
        
        
        if request_body.__contains__('population'):
            N = float(request_body['population'])
            I0 = int(request_body['I0'])
            R0 = float(request_body['R0'])
            incubation_period = int(request_body['incubation_period'])
            infection_period = int(request_body['infection_period'])
            I0 = int(request_body['I0'])
            start = request_body['start_date']
            date = [start]
            beta = [R0/10]


            if R1 != 0:
                date.append(intervention_date)
                beta.append(R1/10)


        else:
            # beta =  globals()['beta_list'].copy()
            
            N = total_population
            I0 = initial_infected
            date = date_list.copy()
            beta = beta_list.copy()
            incubation_period = bd_incubation_period
            infection_period = bd_infection_period

            date.append(intervention_date)
            beta.append(R1/10)
        
        # return Response(I0)
        json_data = parse_data(N, I0, date, beta, incubation_period, infection_period)
        return HttpResponse(json_data)



def parse_data(N, I0, date_list, beta_list, incubation_period, infection_period):
    delta = 1/incubation_period
    miu = 1/infection_period
    I0 = I0/(p*N)
    S0 = 1 - I0

    y0 = S0, E0, I0, R0

    S, E, I, R, beta_info = calculate_data(p, N, y0, date_list, beta_list, t, delta, miu)

    peak = max(I)
    peak_day = I.tolist().index(peak)
    r0_value = beta_list[-1]*10

    data = {
        "beta": beta_info,
        "peak": peak,
        "peak_day": peak_day,
        "confirmed_data": I.tolist(), 
        "r0value": r0_value,
        "information": information,
    }
    json_data = json.dumps(data)

    return json_data


# The SEIR model differential equations.
def deriv_seir(y, t, beta, delta, miu):
    S, E, I, R = y
    dSdt = -beta * S * I
    dEdt = beta * S * I - delta * E
    dIdt = delta * E - miu * I
    dRdt = miu * I
    return dSdt, dEdt, dIdt, dRdt


# The SIR model differential equations.
def deriv_sir(y, t, beta, miu):
    S, E, I, R = y
    dSdt = -beta * S * I
    dIdt = beta * S * I - miu * I
    dRdt = miu * I
    dEdt = 0
    
    return dSdt, dEdt, dIdt, dRdt


def calculate_data(p, N, y, date, beta, t, delta, miu):
    S = [y[0]]
    E = [y[1]]
    I = [y[2]]
    R = [y[3]]
    beta_info = []
    start_date = date[0]

    for i in range(len(date)):
        b = beta[i]
        a1 = get_date_diff(start_date, date[i])
        a2 = len(t)
        beta_info.append(
            {
                "date": date[i],
                "beta": b
            }
        )
        
        if a1==0:
            a1 = 1
            
        if (i+1<len(date)):
            a2 = get_date_diff(start_date, date[i+1])
        
        
        t1 = t[a1-1:a2]
        # print(t1)
        
        y = S[-1], E[-1], I[-1], R[-1]
        if model == 0:        
            ret = odeint(deriv_seir, y, t1, args=(b, delta, miu))
        else:
            ret = odeint(deriv_sir, y, t1, args=(b, miu))

        ret = ret.T
        
        S = np.concatenate((S, ret[0][1:]), axis = 0)
        E = np.concatenate((E, ret[1][1:]), axis = 0)
        I = np.concatenate((I, ret[2][1:]), axis = 0)
        R = np.concatenate((R, ret[3][1:]), axis = 0)
        
    for i in range(len(S)):
        S[i] = math.ceil(S[i]*p*N)
        E[i] = math.ceil(E[i]*p*N)
        I[i] = math.ceil(I[i]*p*N)
        R[i] = math.ceil(R[i]*p*N)
        
    return S, E, I, R, beta_info


def get_date_diff(date1, date2):
    date_1 = datetime.strptime(date1, '%d/%m/%Y')
    date_2 = datetime.strptime(date2, '%d/%m/%Y')
    day = (date_1 - date_2).days
    
    # print("day", day)
    return abs(day)





