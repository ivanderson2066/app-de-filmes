from kivymd.app import MDApp
from kivy.uix.boxlayout import BoxLayout
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDRaisedButton
from kivy.uix.videoplayer import VideoPlayer
from kivy.uix.gridlayout import GridLayout
from kivymd.uix.screen import Screen
from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.uix.video import Video
from kivy.uix.screenmanager import Screen
import requests
from pydub import AudioSegment
from kivy.uix.button import Button
from kivy.uix.screenmanager import Screen
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.video import Video
import os
class FilmeCard(BoxLayout):
    def __init__(self, titulo, genero, duracao, url, screen_manager, **kwargs):
        super().__init__(**kwargs)
        self.orientation = "vertical"
        self.size_hint = (0.5, None)
        self.height = "250dp"  

        self.label_titulo = MDLabel(text=titulo, halign="center")
        self.label_genero = MDLabel(text=genero, halign="center")
        self.label_duracao = MDLabel(text=f"{duracao} min", halign="center")
        self.botao_reproduzir = MDRaisedButton(text="Assistir vídeo", on_release=self.abrir_tela_reproducao, md_bg_color=(0, 0, 0, 0), pos_hint={'center_x': 0.5, 'center_y': 0.5})

        self.add_widget(self.label_titulo)
        self.add_widget(self.label_genero)
        self.add_widget(self.label_duracao)
        self.add_widget(self.botao_reproduzir)

        self.url = url
        self.screen_manager = screen_manager

    def abrir_tela_reproducao(self, *args):
        self.screen_manager.current = "reproducao"
        self.screen_manager.get_screen("reproducao").reproduzir_video(self.url)

class FilmesGrid(GridLayout):
    def __init__(self, screen_manager, **kwargs):
        super().__init__(**kwargs)
        self.cols = 2
        self.spacing = "20dp"
        self.screen_manager = screen_manager

        # Lista de filmes com informações
        filmes = [
            {
                "titulo": "Gato de Botas 2: O Último Pedido",
                "genero": "Ação",
                "duracao": 100,
                "url": "https://www.dropbox.com/scl/fi/zsl0uzcsflpbmy01aggi2/gato-de-botas-2.mp4?rlkey=ndp990nddnjc5zn0ks8s6ynu7&dl=1"
            },
            {
                "titulo": "Pantera Negra",
                "genero": "Ação",
                "duracao": 134,
                "url":""

               
            },
            {
                "titulo": "Toy Story 4",
                "genero": "Animação",
                "duracao": 100, 
                "url":""

            }
        ]

        for filme_info in filmes:
            cartao = FilmeCard(screen_manager=screen_manager, **filme_info)
            self.add_widget(cartao)


class TelaReproducao(Screen):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.video_player = VideoPlayer(source='', state='stop', options={'allow_stretch': True})
        self.video_player.bind(on_error=self.on_video_error)
        self.played_duration = 0  # Variável para armazenar a duração reproduzida

        # Adiciona o reprodutor de vídeo à tela
        self.add_widget(self.video_player)

    def on_video_error(self, instance, value):
        print(f"Erro de vídeo: {value}")
        self.video_player.state = 'stop'  # Parar a reprodução em caso de erro

    def reproduzir_video(self, url):
        if url:
            try:
                # Baixar o conteúdo do vídeo antecipadamente
                video_content = requests.get(url).content

                # Verificar se o conteúdo do vídeo está vazio
                if not video_content:
                    print("Erro: O fluxo de vídeo não contém dados.")
                    return

                # Gravar o conteúdo do vídeo em um arquivo temporário
                with open("temp_video.mp4", "wb") as video_file:
                    video_file.write(video_content)

                # Definir o caminho local para o vídeo baixado
                local_video_path = "temp_video.mp4"

                # Definir a origem para o reprodutor de vídeo
                self.video_player.source = local_video_path
                self.video_player.state = 'play'
            except Exception as e:
                print(f"Erro: {e}")
        else:
            print("Erro: URL do vídeo não fornecida.")

    def pause_video(self, *args):
        if self.video_player.state == 'play':
            self.video_player.state = 'pause'
        elif self.video_player.state == 'pause':
            self.video_player.state = 'play'

    def delete_played_parts(self, *args):
        if self.played_duration > 0:
            try:
                # Corta o arquivo de vídeo para remover a parte reproduzida
                caminho_original = "temp_video.mp4"
                caminho_cortado = "video_cortado.mp4"

                # Use uma biblioteca ou ferramenta para cortar o arquivo de vídeo
                # Para simplicidade, estou usando pydub aqui
                audio_original = AudioSegment.from_file(caminho_original)
                audio_cortado = audio_original[self.played_duration * 1000:]  # Converter para milissegundos
                audio_cortado.export(caminho_cortado, format="mp4")

                # Define o vídeo cortado como a nova origem
                self.video_player.source = caminho_cortado
                self.video_player.state = 'play'
            except Exception as e:
                print(f"Erro ao deletar partes reproduzidas: {e}")
        else:
            print("Nenhuma parte reproduzida para deletar.")

    def exit_video(self, *args):
        self.video_player.state = 'stop'
        self.video_player.source = ''  # Limpar a origem do vídeo

        # Excluir arquivo temporário
        self.delete_temp_video()

    def delete_temp_video(self):
        temp_video_path = "temp_video.mp4"
        if os.path.exists(temp_video_path):
            try:
                os.remove(temp_video_path)
                print("Arquivo temporário excluído com sucesso.")
            except Exception as e:
                print(f"Erro ao excluir o arquivo temporário: {e}")
        else:
            print("Nenhum arquivo temporário encontrado.")
class AppNetflix(MDApp):
    def build(self):
        screen_manager = ScreenManager()

        filmes_screen = Screen(name="filmes")
        filmes_screen.add_widget(FilmesGrid(screen_manager=screen_manager))
        screen_manager.add_widget(filmes_screen)

        reproducao_screen = TelaReproducao(name="reproducao")
        screen_manager.add_widget(reproducao_screen)

        return screen_manager

    def on_stop(self):
        reproducao_screen = self.root.get_screen("reproducao")
        reproducao_screen.delete_temp_video()

if __name__ == "__main__":
    AppNetflix().run()
