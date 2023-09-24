INSERT INTO public.User (login, password, role_)
VALUES ('Ivan', encode('Ivan2pass', 'base64')::bytea, 'EMPLOYEE');

INSERT INTO public.User (login, password, role_)
VALUES ('Bob', encode('Bob7pass', 'base64')::bytea, 'CUSTOMER');

INSERT INTO public.Card (user_login, bonuses)
VALUES ('Bob', 500);

INSERT INTO public.Manufacturer (id, name_)
VALUES ('2f109076-e8d2-11ed-a05b-0242ac120003', 'TAKAMINE');

INSERT INTO public.Product (id, name_, price, manufacturer_id, storage_cnt)
VALUES ('afe83240-35cb-4825-a33a-e689c059db4b', 'TAKAMINE-FG46', 30000, '2f109076-e8d2-11ed-a05b-0242ac120003', 10);

INSERT INTO public.Product (id, name_, price, manufacturer_id, storage_cnt, characteristics)
VALUES ('c23bbab0-3df8-429c-9b07-9d41ac86a2ca', 'TAKAMINE-FG50', 35000, '2f109076-e8d2-11ed-a05b-0242ac120003',
        10, '{"cnt_frets": "24"}');

INSERT INTO public.deliverypoint (id, address)
VALUES ('b92f19d8-8db0-498f-8ee9-ce2580706708', 'Петровский проспект, 24');


